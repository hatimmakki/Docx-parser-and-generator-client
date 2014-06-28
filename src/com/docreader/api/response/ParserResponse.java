package com.docreader.api.response;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ParserResponse extends BaseResponse {
    
    public enum Type {
        @SerializedName("text")
        TEXT, @SerializedName("edit")
        EDIT, @SerializedName("date")
        DATE, @SerializedName("image")
        IMAGE
    }
    
    public class ParserResponseItem {

        public class Data {
            private String text;
            private String hint;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getHint() {
                return hint;
            }

            public void setHint(String hint) {
                this.hint = hint;
            }

        }

        private Type type;
        private String key;
        private Data data;

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }
    }

    private List<ParserResponseItem> data;

    public List<ParserResponseItem> getData() {
        return data;
    }
    
}
