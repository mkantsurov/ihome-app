package technology.positivehome.ihome.server.model;

import technology.positivehome.ihome.domain.constant.SearchField;

import java.util.ArrayList;
import java.util.List;


public class SearchParam {

    public static final String PREDICAT_TRUE = "true";
    public static final  String PREDICAT_FALSE = "false";
    public static final  String PREDICAT_NOT_TRUE = "is not true";
    public static final String PREDICAT_LIKE = "like";
    public static final String PREDICAT_NOT_LIKE = "not like";
    public static final String PREDICAT_ILIKE = "ilike";
    public static final String PREDICAT_NOT_ILIKE = "not ilike";
    public static final String PREDICAT_L_LIKE = "llike";
    public static final String PREDICAT_R_LIKE = "rlike";
    public static final String PREDICAT_L_ILIKE = "lIlike";
    public static final String PREDICAT_R_ILIKE = "rIlike";
    public static final String PREDICAT_GTE = ">=";
    public static final String PREDICAT_GT = ">";
    public static final String PREDICAT_LTE = "<=";
    public static final String PREDICAT_LT = "<";
    public static final String PREDICAT_IN = "in";
    public static final String PREDICAT_NOT_IN = "not in";
    public static final String PREDICAT_EQ = "=";
    public static final String PREDICAT_NOT_EQ = "!=";
    public static final String PREDICAT_NULL = "is null";

    public static final String PREDICAT_NOT_NULL = "is not null";
    private SearchField key;
    private List<String> values;
    private String predicat; // "true" = match, "false" = not match, "like" = '%XXX%', "llike" = '%XXX', "rlike" = 'XXX%'

    public SearchParam() {
        predicat = PREDICAT_TRUE;
    }

    public SearchParam(SearchField key, String... val) {
        this();
        this.key = key;
        if (val.length > 1) {
            this.predicat = PREDICAT_IN;
        }
        if (val.length > 0) {
            this.values = new ArrayList<>();
            for (String value : val) {
                this.values.add(DBTrim(value));
            }
        }
    }
    public SearchParam(String predicat, SearchField key, String... value) {
        this(key, value);
        this.predicat = predicat;
    }

    private String DBTrim(String value) {
        if (value == null) {
            return "";
        }
        value = value.trim();
        return value;
    }

    public SearchField getKey() {
        return key;
    }

    public void setKey(SearchField key) {
        this.key = key;
    }

    public String getPredicat() {
        return predicat;
    }

    public void setPredicat(String predicat) {
        this.predicat = predicat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchParam that = (SearchParam) o;

        if (key != that.key) return false;
        if (values != null ? !values.equals(that.values) : that.values != null) return false;
        return predicat != null ? predicat.equals(that.predicat) : that.predicat == null;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (values != null ? values.hashCode() : 0);
        result = 31 * result + (predicat != null ? predicat.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (key == null || predicat == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder("");
        if (values != null) {
            for (String value : values) {
                builder.append(value);
            }
        }

        return (new StringBuilder(key.name()).append(" ").append(predicat).append(" ").append(builder.toString())).toString();
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
