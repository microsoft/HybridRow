namespace Microsoft.Azure.Cosmos.Core.Collections
{
    using System;
    using System.Runtime.CompilerServices;

    /// <summary>A trie, otherwise known as a prefix tree.</summary>
    /// <remarks>
    /// A trie is a map from key to value. Keys are formed by a sequence of symbols in some alphabet.
    /// Values are merely round-tripped by the data structure and are not otherwise interpreted.
    /// <p>
    /// Keys and symbols are compared using the binary collation (i.e. memcmp). Symbols must therefore
    /// be an unmanaged type (i.e. convertible to byte).
    /// </p>
    /// <p>See https://en.wikipedia.org/wiki/Trie for a detailed description of the Trie data structure.</p>
    /// </remarks>
    /// <typeparam name="TSymbol">The type of the individual elements that form a key.</typeparam>
    /// <typeparam name="TValue">The type of the value.</typeparam>
    public class Trie<TSymbol, TValue>
        where TSymbol : unmanaged
    {
        /// <summary>The smallest capacity allocated.</summary>
        private const int MinCapacity = 20;

        /// <summary>All available allocated storage space.</summary>
        private Memory<Node> capacity;

        /// <summary>
        /// A view of a subset of <see cref="capacity" /> that forms the current active nodes of the
        /// tree.
        /// </summary>
        /// <remarks>
        /// The trie is encoded as a variable branching n-ary tree of <see cref="Node" />'s.  The tree begins
        /// at the root (which has no symbol).  Each non-root node encodes a single symbol.  The path from the
        /// root to a node encodes a unique sequence of symbols. If the sequence represents a key within the
        /// trie then its terminal flag is set and it also stores a value. Interior nodes of the tree may be
        /// terminal (implying that two keys, one of which is a proper prefix of the other, both exist within
        /// the trie).
        /// <p>
        /// Children of a node form a linked list, the head of which is pointed to by the
        /// <see cref="Node.Child" /> and the siblings of which are linked together via
        /// <see cref="Node.Next" />.
        /// </p>
        /// <p>
        /// The 0'th element of this view is always the root node which will store the value for the empty
        /// sequence (if it has been added). Both the children linked list and the n-ary tree left nodes are
        /// terminated by the address 0, which can never be a valid child because the root is always in
        /// position 0.
        /// </p>
        /// </remarks>
        private Memory<Node> tree;

        /// <summary>Initializes a new instance of the <see cref="Trie{TSymbol, TValue}" /> class.</summary>
        /// <param name="initialCapacity">The (optional) initial capacity.</param>
        public Trie(int initialCapacity = 0)
        {
            Contract.Requires(initialCapacity >= 0);

            // Allocate some initial capacity.
            this.capacity = new Node[initialCapacity < Trie<TSymbol, TValue>.MinCapacity ? Trie<TSymbol, TValue>.MinCapacity : initialCapacity];

            // Insert the root.  The root will store the value for the empty sequence.
            this.tree = this.capacity.Slice(0, 1);
        }

        /// <summary>Add an item to the trie.</summary>
        /// <param name="key">The key to be added.</param>
        /// <param name="value">The value to be associated with the key.</param>
        /// <returns>True if the item was added, false if the key already exists in the trie.</returns>
        public bool TryAdd(ReadOnlySpan<TSymbol> key, TValue value)
        {
            int cur = 0;
            foreach (TSymbol s in key)
            {
                if (this.FindChild(s, cur, out int child))
                {
                    cur = child;
                }
                else
                {
                    int address = this.AllocNode(s);
                    if (child == 0)
                    {
                        this.tree.Span[cur].Child = address;
                    }
                    else
                    {
                        this.tree.Span[child].Next = address;
                    }

                    cur = address;
                }
            }

            if ((this.tree.Span[cur].Flags & Flags.Terminal) != 0)
            {
                // Already exists.
                return false;
            }

            this.tree.Span[cur].Flags |= Flags.Terminal;
            this.tree.Span[cur].Value = value;
            return true;
        }

        /// <summary>Add an item to the trie. if the key exists, replace the existing item.</summary>
        /// <param name="key">The key to be added.</param>
        /// <param name="value">The value to be associated with the key.</param>
        public void AddOrUpdate(ReadOnlySpan<TSymbol> key, TValue value)
        {
            int cur = 0;
            foreach (TSymbol s in key)
            {
                if (this.FindChild(s, cur, out int child))
                {
                    cur = child;
                }
                else
                {
                    int address = this.AllocNode(s);
                    if (child == 0)
                    {
                        this.tree.Span[cur].Child = address;
                    }
                    else
                    {
                        this.tree.Span[child].Next = address;
                    }

                    cur = address;
                }
            }

            this.tree.Span[cur].Flags |= Flags.Terminal;
            this.tree.Span[cur].Value = value;
        }

        /// <summary>Find an item within a trie.</summary>
        /// <param name="key">The key to be added.</param>
        /// <param name="value">
        /// If <paramref name="key" /> was found then the value to be associated with the
        /// key, otherwise default.
        /// </param>
        /// <returns>True if the item was found, false otherwise.</returns>
        public bool TryGetValue(ReadOnlySpan<TSymbol> key, out TValue value)
        {
            int cur = 0;
            foreach (TSymbol s in key)
            {
                if (!this.FindChild(s, cur, out cur))
                {
                    value = default;
                    return false;
                }
            }

            if ((this.tree.Span[cur].Flags & Flags.Terminal) == 0)
            {
                value = default;
                return false;
            }

            value = this.tree.Span[cur].Value;
            return true;
        }

        /// <summary>Safe memory compare of <see cref="TSymbol" />.</summary>
        /// <param name="left">The left argument.</param>
        /// <param name="right">The right argument.</param>
        /// <returns>
        /// True if the byte sequence of <paramref name="left" /> exactly matches the byte sequence of
        /// <paramref name="right" />.
        /// </returns>
        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        private static unsafe bool SymbolEquals(TSymbol left, TSymbol right)
        {
            ReadOnlySpan<byte> p = new ReadOnlySpan<byte>(&left, sizeof(TSymbol));
            ReadOnlySpan<byte> q = new ReadOnlySpan<byte>(&right, sizeof(TSymbol));
            return p.SequenceEqual(q);
        }

        /// <summary>Add a new unattached node to the tree.</summary>
        /// <param name="s">The symbol for the node.</param>
        /// <returns>The address of the new node.</returns>
        private int AllocNode(TSymbol s)
        {
            int node = this.tree.Length;
            if (this.tree.Length == this.capacity.Length)
            {
                this.capacity = new Node[this.capacity.Length * 2];
                this.tree.CopyTo(this.capacity);
            }

            this.tree = this.capacity.Slice(0, this.tree.Length + 1);

            this.tree.Span[node] = new Node(Flags.None, s, 0, 0, default);
            return node;
        }

        /// <summary>Find a child node of <paramref name="parent" /> with a matching symbol.</summary>
        /// <param name="s">The symbol to match.</param>
        /// <param name="parent">The address of the parent.</param>
        /// <param name="child">
        /// If successful, the address of the child, otherwise the address of the previous
        /// in the child link list where a new child should be added.  0 if the parent has no children.
        /// </param>
        /// <returns>True if a child with matching symbol was found, false otherwise.</returns>
        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        private bool FindChild(TSymbol s, int parent, out int child)
        {
            int last = 0;
            child = this.tree.Span[parent].Child;
            while (child != 0)
            {
                if (typeof(TSymbol) == typeof(byte))
                {
                    unsafe
                    {
                        TSymbol t = this.tree.Span[child].Symbol;
                        if (*(byte*)&t == *(byte*)&s)
                        {
                            return true;
                        }
                    }
                }
                else if (Trie<TSymbol, TValue>.SymbolEquals(this.tree.Span[child].Symbol, s))
                {
                    return true;
                }

                last = child;
                child = this.tree.Span[child].Next;
            }

            child = last;
            return false;
        }

        private struct Node
        {
            /// <summary>Flags indicating characteristics of the node.</summary>
            public Flags Flags;

            /// <summary>The symbol forming the key prefix added by this node.</summary>
            public readonly TSymbol Symbol;

            /// <summary>A pointer to the index of the first child node of this node.</summary>
            public int Child;

            /// <summary>
            /// A pointer to the index of the next sibling of this node (next child of this node's
            /// parent).
            /// </summary>
            public int Next;

            /// <summary>
            /// If the current node is a terminal, the value associated with current prefix, otherwise
            /// undefined.
            /// </summary>
            public TValue Value;

            public Node(Flags flags, TSymbol symbol, int child, int next, TValue value)
            {
                this.Flags = flags;
                this.Symbol = symbol;
                this.Child = child;
                this.Next = next;
                this.Value = value;
            }
        }

        [Flags]
        private enum Flags
        {
            /// <summary>Interior node within the trie that carries no value.</summary>
            None = 0,

            /// <summary>Either interior or left node within the trie that has a value.</summary>
            Terminal = 1
        }
    }
}
