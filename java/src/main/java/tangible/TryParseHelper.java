// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package tangible;

import com.azure.data.cosmos.core.Out;

public final class TryParseHelper {
    public static boolean tryParseBoolean(String s, Out<Boolean> result) {
        try {
            result.setAndGet(Boolean.parseBoolean(s));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean tryParseByte(String s, Out<Byte> result) {
        try {
            result.setAndGet(Byte.parseByte(s));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean tryParseDouble(String s, Out<Double> result) {
        try {
            result.setAndGet(Double.parseDouble(s));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean tryParseFloat(String s, Out<Float> result) {
        try {
            result.setAndGet(Float.parseFloat(s));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean tryParseInt(String s, Out<Integer> result) {
        try {
            result.setAndGet(Integer.parseInt(s));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean tryParseLong(String s, Out<Long> result) {
        try {
            result.setAndGet(Long.parseLong(s));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean tryParseShort(String s, Out<Short> result) {
        try {
            result.setAndGet(Short.parseShort(s));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}