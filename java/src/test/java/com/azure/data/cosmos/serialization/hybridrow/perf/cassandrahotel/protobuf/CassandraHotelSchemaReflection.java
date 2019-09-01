// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.data.cosmos.serialization.hybridrow.perf.cassandrahotel.protobuf;

import com.azure.data.cosmos.serialization.hybridrow.perf.*;

// <auto-generated>
//     Generated by the protocol buffer compiler.  DO NOT EDIT!
//     source: TestData/CassandraHotelSchema.proto
// </auto-generated>

//C# TO JAVA CONVERTER NOTE: There is no Java equivalent to C# namespace aliases:
//using pb = Google.Protobuf;
//C# TO JAVA CONVERTER NOTE: There is no Java equivalent to C# namespace aliases:
//using pbc = Google.Protobuf.Collections;
//C# TO JAVA CONVERTER NOTE: There is no Java equivalent to C# namespace aliases:
//using pbr = Google.Protobuf.Reflection;


/**
 * Holder for reflection information generated from TestData/CassandraHotelSchema.proto
 */
public final class CassandraHotelSchemaReflection {

    // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
    ///#region Descriptor

    private static Google.Protobuf.Reflection.FileDescriptor descriptor;

    static {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: byte[] descriptorData = System.Convert.FromBase64String(string.Concat
        // ("CiNUZXN0RGF0YS9DYXNzYW5kcmFIb3RlbFNjaGVtYS5wcm90bxJITWljcm9z",
        // "b2Z0LkF6dXJlLkNvc21vcy5TZXJpYWxpemF0aW9uLkh5YnJpZFJvdy5UZXN0",
        // "cy5QZXJmLkNhc3NhbmRyYUhvdGVsGh5nb29nbGUvcHJvdG9idWYvd3JhcHBl",
        // "cnMucHJvdG8iYgoKUG9zdGFsQ29kZRIoCgN6aXAYASABKAsyGy5nb29nbGUu",
        // "cHJvdG9idWYuSW50MzJWYWx1ZRIqCgVwbHVzNBgCIAEoCzIbLmdvb2dsZS5w",
        // "cm90b2J1Zi5JbnQzMlZhbHVlIvsBCgdBZGRyZXNzEiwKBnN0cmVldBgBIAEo",
        // "CzIcLmdvb2dsZS5wcm90b2J1Zi5TdHJpbmdWYWx1ZRIqCgRjaXR5GAIgASgL",
        // "MhwuZ29vZ2xlLnByb3RvYnVmLlN0cmluZ1ZhbHVlEisKBXN0YXRlGAMgASgL",
        // "MhwuZ29vZ2xlLnByb3RvYnVmLlN0cmluZ1ZhbHVlEmkKC3Bvc3RhbF9jb2Rl",
        // "GAQgASgLMlQuTWljcm9zb2Z0LkF6dXJlLkNvc21vcy5TZXJpYWxpemF0aW9u",
        // "Lkh5YnJpZFJvdy5UZXN0cy5QZXJmLkNhc3NhbmRyYUhvdGVsLlBvc3RhbENv",
        // "ZGUi9QEKBkhvdGVscxIuCghob3RlbF9pZBgBIAEoCzIcLmdvb2dsZS5wcm90",
        // "b2J1Zi5TdHJpbmdWYWx1ZRIqCgRuYW1lGAIgASgLMhwuZ29vZ2xlLnByb3Rv",
        // "YnVmLlN0cmluZ1ZhbHVlEisKBXBob25lGAMgASgLMhwuZ29vZ2xlLnByb3Rv",
        // "YnVmLlN0cmluZ1ZhbHVlEmIKB2FkZHJlc3MYBCABKAsyUS5NaWNyb3NvZnQu",
        // "QXp1cmUuQ29zbW9zLlNlcmlhbGl6YXRpb24uSHlicmlkUm93LlRlc3RzLlBl",
        // "cmYuQ2Fzc2FuZHJhSG90ZWwuQWRkcmVzcyLeAQodQXZhaWxhYmxlX1Jvb21z",
        // "X0J5X0hvdGVsX0RhdGUSLgoIaG90ZWxfaWQYASABKAsyHC5nb29nbGUucHJv",
        // "dG9idWYuU3RyaW5nVmFsdWUSKQoEZGF0ZRgCIAEoCzIbLmdvb2dsZS5wcm90",
        // "b2J1Zi5JbnQ2NFZhbHVlEjAKC3Jvb21fbnVtYmVyGAMgASgLMhsuZ29vZ2xl",
        // "LnByb3RvYnVmLkludDMyVmFsdWUSMAoMaXNfYXZhaWxhYmxlGAQgASgLMhou",
        // "Z29vZ2xlLnByb3RvYnVmLkJvb2xWYWx1ZSKfBAoGR3Vlc3RzEi4KCGd1ZXN0",
        // "X2lkGAEgASgLMhwuZ29vZ2xlLnByb3RvYnVmLlN0cmluZ1ZhbHVlEjAKCmZp",
        // "cnN0X25hbWUYAiABKAsyHC5nb29nbGUucHJvdG9idWYuU3RyaW5nVmFsdWUS",
        // "LwoJbGFzdF9uYW1lGAMgASgLMhwuZ29vZ2xlLnByb3RvYnVmLlN0cmluZ1Zh",
        // "bHVlEisKBXRpdGxlGAQgASgLMhwuZ29vZ2xlLnByb3RvYnVmLlN0cmluZ1Zh",
        // "bHVlEg4KBmVtYWlscxgFIAMoCRIVCg1waG9uZV9udW1iZXJzGAYgAygJEnIK",
        // "CWFkZHJlc3NlcxgHIAMoCzJfLk1pY3Jvc29mdC5BenVyZS5Db3Ntb3MuU2Vy",
        // "aWFsaXphdGlvbi5IeWJyaWRSb3cuVGVzdHMuUGVyZi5DYXNzYW5kcmFIb3Rl",
        // "bC5HdWVzdHMuQWRkcmVzc2VzRW50cnkSNAoOY29uZmlybV9udW1iZXIYCCAB",
        // "KAsyHC5nb29nbGUucHJvdG9idWYuU3RyaW5nVmFsdWUagwEKDkFkZHJlc3Nl",
        // "c0VudHJ5EgsKA2tleRgBIAEoCRJgCgV2YWx1ZRgCIAEoCzJRLk1pY3Jvc29m",
        // "dC5BenVyZS5Db3Ntb3MuU2VyaWFsaXphdGlvbi5IeWJyaWRSb3cuVGVzdHMu",
        // "UGVyZi5DYXNzYW5kcmFIb3RlbC5BZGRyZXNzOgI4AUJUqgJRTWljcm9zb2Z0",
        // "LkF6dXJlLkNvc21vcy5TZXJpYWxpemF0aW9uLkh5YnJpZFJvdy5UZXN0cy5Q",
        // "ZXJmLkNhc3NhbmRyYUhvdGVsLlByb3RvYnVmYgZwcm90bzM="));
        byte[] descriptorData = System.Convert.FromBase64String(String.Concat(
            "CiNUZXN0RGF0YS9DYXNzYW5kcmFIb3RlbFNjaGVtYS5wcm90bxJITWljcm9z",
            "b2Z0LkF6dXJlLkNvc21vcy5TZXJpYWxpemF0aW9uLkh5YnJpZFJvdy5UZXN0",
            "cy5QZXJmLkNhc3NhbmRyYUhvdGVsGh5nb29nbGUvcHJvdG9idWYvd3JhcHBl",
            "cnMucHJvdG8iYgoKUG9zdGFsQ29kZRIoCgN6aXAYASABKAsyGy5nb29nbGUu",
            "cHJvdG9idWYuSW50MzJWYWx1ZRIqCgVwbHVzNBgCIAEoCzIbLmdvb2dsZS5w",
            "cm90b2J1Zi5JbnQzMlZhbHVlIvsBCgdBZGRyZXNzEiwKBnN0cmVldBgBIAEo",
            "CzIcLmdvb2dsZS5wcm90b2J1Zi5TdHJpbmdWYWx1ZRIqCgRjaXR5GAIgASgL",
            "MhwuZ29vZ2xlLnByb3RvYnVmLlN0cmluZ1ZhbHVlEisKBXN0YXRlGAMgASgL",
            "MhwuZ29vZ2xlLnByb3RvYnVmLlN0cmluZ1ZhbHVlEmkKC3Bvc3RhbF9jb2Rl",
            "GAQgASgLMlQuTWljcm9zb2Z0LkF6dXJlLkNvc21vcy5TZXJpYWxpemF0aW9u",
            "Lkh5YnJpZFJvdy5UZXN0cy5QZXJmLkNhc3NhbmRyYUhvdGVsLlBvc3RhbENv",
            "ZGUi9QEKBkhvdGVscxIuCghob3RlbF9pZBgBIAEoCzIcLmdvb2dsZS5wcm90",
            "b2J1Zi5TdHJpbmdWYWx1ZRIqCgRuYW1lGAIgASgLMhwuZ29vZ2xlLnByb3Rv",
            "YnVmLlN0cmluZ1ZhbHVlEisKBXBob25lGAMgASgLMhwuZ29vZ2xlLnByb3Rv",
            "YnVmLlN0cmluZ1ZhbHVlEmIKB2FkZHJlc3MYBCABKAsyUS5NaWNyb3NvZnQu",
            "QXp1cmUuQ29zbW9zLlNlcmlhbGl6YXRpb24uSHlicmlkUm93LlRlc3RzLlBl",
            "cmYuQ2Fzc2FuZHJhSG90ZWwuQWRkcmVzcyLeAQodQXZhaWxhYmxlX1Jvb21z",
            "X0J5X0hvdGVsX0RhdGUSLgoIaG90ZWxfaWQYASABKAsyHC5nb29nbGUucHJv",
            "dG9idWYuU3RyaW5nVmFsdWUSKQoEZGF0ZRgCIAEoCzIbLmdvb2dsZS5wcm90",
            "b2J1Zi5JbnQ2NFZhbHVlEjAKC3Jvb21fbnVtYmVyGAMgASgLMhsuZ29vZ2xl",
            "LnByb3RvYnVmLkludDMyVmFsdWUSMAoMaXNfYXZhaWxhYmxlGAQgASgLMhou",
            "Z29vZ2xlLnByb3RvYnVmLkJvb2xWYWx1ZSKfBAoGR3Vlc3RzEi4KCGd1ZXN0",
            "X2lkGAEgASgLMhwuZ29vZ2xlLnByb3RvYnVmLlN0cmluZ1ZhbHVlEjAKCmZp",
            "cnN0X25hbWUYAiABKAsyHC5nb29nbGUucHJvdG9idWYuU3RyaW5nVmFsdWUS",
            "LwoJbGFzdF9uYW1lGAMgASgLMhwuZ29vZ2xlLnByb3RvYnVmLlN0cmluZ1Zh",
            "bHVlEisKBXRpdGxlGAQgASgLMhwuZ29vZ2xlLnByb3RvYnVmLlN0cmluZ1Zh",
            "bHVlEg4KBmVtYWlscxgFIAMoCRIVCg1waG9uZV9udW1iZXJzGAYgAygJEnIK",
            "CWFkZHJlc3NlcxgHIAMoCzJfLk1pY3Jvc29mdC5BenVyZS5Db3Ntb3MuU2Vy",
            "aWFsaXphdGlvbi5IeWJyaWRSb3cuVGVzdHMuUGVyZi5DYXNzYW5kcmFIb3Rl",
            "bC5HdWVzdHMuQWRkcmVzc2VzRW50cnkSNAoOY29uZmlybV9udW1iZXIYCCAB",
            "KAsyHC5nb29nbGUucHJvdG9idWYuU3RyaW5nVmFsdWUagwEKDkFkZHJlc3Nl",
            "c0VudHJ5EgsKA2tleRgBIAEoCRJgCgV2YWx1ZRgCIAEoCzJRLk1pY3Jvc29m",
            "dC5BenVyZS5Db3Ntb3MuU2VyaWFsaXphdGlvbi5IeWJyaWRSb3cuVGVzdHMu",
            "UGVyZi5DYXNzYW5kcmFIb3RlbC5BZGRyZXNzOgI4AUJUqgJRTWljcm9zb2Z0",
            "LkF6dXJlLkNvc21vcy5TZXJpYWxpemF0aW9uLkh5YnJpZFJvdy5UZXN0cy5Q",
            "ZXJmLkNhc3NhbmRyYUhvdGVsLlByb3RvYnVmYgZwcm90bzM="));
        descriptor = Google.Protobuf.Reflection.FileDescriptor.FromGeneratedCode(descriptorData,
            new Google.Protobuf.Reflection.FileDescriptor[] { Google.Protobuf.WellKnownTypes.WrappersReflection.Descriptor }, new Google.Protobuf.Reflection.GeneratedClrTypeInfo(null, new Google.Protobuf.Reflection.GeneratedClrTypeInfo[] { new Google.Protobuf.Reflection.GeneratedClrTypeInfo(PostalCode.class, PostalCode.getParser(), new String[] { "Zip", "Plus4" }, null, null, null), new Google.Protobuf.Reflection.GeneratedClrTypeInfo(Address.class, Address.getParser(), new String[] { "Street", "City", "State", "PostalCode" }, null, null, null), new Google.Protobuf.Reflection.GeneratedClrTypeInfo(Hotels.class, Hotels.getParser(), new String[] { "HotelId", "Name", "Phone", "Address" }, null, null, null), new Google.Protobuf.Reflection.GeneratedClrTypeInfo(Available_Rooms_By_Hotel_Date.class, Available_Rooms_By_Hotel_Date.getParser(), new String[] { "HotelId", "Date", "RoomNumber", "IsAvailable" }, null, null, null), new Google.Protobuf.Reflection.GeneratedClrTypeInfo(Guests.class, Guests.getParser(), new String[] { "GuestId", "FirstName", "LastName", "Title", "Emails", "PhoneNumbers", "Addresses", "ConfirmNumber" }, null, null, new Google.Protobuf.Reflection.GeneratedClrTypeInfo[] { null }) }));
    }

    /**
     * File descriptor for TestData/CassandraHotelSchema.proto
     */
    public static Google.Protobuf.Reflection.FileDescriptor getDescriptor() {
        return descriptor;
    }
    // TODO: C# TO JAVA CONVERTER: There is no preprocessor in Java:
    ///#endregion

}