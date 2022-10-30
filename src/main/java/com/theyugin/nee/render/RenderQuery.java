package com.theyugin.nee.render;

import java.util.Objects;

public class RenderQuery {
    public final RenderType renderType;
    public final String query;

    public RenderQuery(RenderType renderType, String query) {
        this.renderType = renderType;
        this.query = query;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenderQuery that = (RenderQuery) o;
        return renderType == that.renderType && query.equals(that.query);
    }

    @Override
    public int hashCode() {
        return Objects.hash(renderType, query);
    }
}
