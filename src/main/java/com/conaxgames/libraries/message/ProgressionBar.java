package com.conaxgames.libraries.message;

public final class ProgressionBar {

    private int current;
    private int max;
    private int length = 20;
    private char symbol = '-';
    private String completedColor = "&a";
    private String remainingColor = "&7";
    private boolean brackets;
    private String bracketColor = "&8";
    private char openBracket = '[';
    private char closeBracket = ']';
    private boolean percent;
    private String percentColor = "&7";
    private String suffix;

    private ProgressionBar() {}

    public static ProgressionBar builder() {
        return new ProgressionBar();
    }

    public ProgressionBar current(int current) {
        this.current = current;
        return this;
    }

    public ProgressionBar max(int max) {
        this.max = max;
        return this;
    }

    public ProgressionBar length(int length) {
        this.length = length;
        return this;
    }

    public ProgressionBar symbol(char symbol) {
        this.symbol = symbol;
        return this;
    }

    public ProgressionBar completedColor(String completedColor) {
        this.completedColor = completedColor;
        return this;
    }

    public ProgressionBar remainingColor(String remainingColor) {
        this.remainingColor = remainingColor;
        return this;
    }

    public ProgressionBar brackets(boolean brackets) {
        this.brackets = brackets;
        return this;
    }

    public ProgressionBar brackets(char openBracket, char closeBracket) {
        this.brackets = true;
        this.openBracket = openBracket;
        this.closeBracket = closeBracket;
        return this;
    }

    public ProgressionBar bracketColor(String bracketColor) {
        this.bracketColor = bracketColor;
        return this;
    }

    public ProgressionBar percent(boolean percent) {
        this.percent = percent;
        return this;
    }

    public ProgressionBar percentColor(String percentColor) {
        this.percentColor = percentColor;
        return this;
    }

    public ProgressionBar suffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public String build() {
        int len = Math.max(length, 0);
        int safeMax = Math.max(max, 0);
        float ratio = safeMax > 0 ? (float) Math.clamp(current, 0, safeMax) / safeMax : 0f;
        int filled = (int) (len * ratio);
        int empty = len - filled;

        String sym = String.valueOf(symbol);
        StringBuilder out = new StringBuilder();
        if (brackets) out.append(bracketColor).append(openBracket);
        out.append((completedColor + sym).repeat(filled)).append((remainingColor + sym).repeat(empty));
        if (brackets) out.append(bracketColor).append(closeBracket);
        if (percent) out.append(' ').append(percentColor).append(Math.round(ratio * 100)).append('%');
        if (suffix != null) out.append(suffix);
        return CC.translate(out.toString());
    }

    @Override
    public String toString() {
        return build();
    }
}
