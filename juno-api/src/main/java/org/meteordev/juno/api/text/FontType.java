package org.meteordev.juno.api.text;

public enum FontType {
    Regular,
    Bold,
    Italic,
    BoldItalic;

    public static FontType fromString(String str) {
        switch (str) {
            case "Bold":        return Bold;
            case "Italic":      return Italic;
            case "Bold Italic":
            case "BoldItalic":  return BoldItalic;
            default:            return Regular;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case Bold:          return "Bold";
            case Italic:        return "Italic";
            case BoldItalic:    return "Bold Italic";
            default:            return "Regular";
        }
    }
}
