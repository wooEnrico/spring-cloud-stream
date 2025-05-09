package com.example.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse(long timestamp, int status, String error, String message, Object data) {
    @JsonCreator
    public ApiResponse {
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public long getDuration() {
        return System.currentTimeMillis() - timestamp;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "timestamp=" + timestamp +
                ", status=" + status +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long timestamp;
        private int status;
        private String error;
        private String message;
        private Object data;

        public long getTimestamp() {
            return timestamp;
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public int getStatus() {
            return status;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public String getError() {
            return error;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public String getMessage() {
            return message;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Object getData() {
            return data;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public ApiResponse build() {
            assert timestamp > 0 : "Timestamp must be greater than 0";
            assert status > 0 : "Status must be greater than 0";
            return new ApiResponse(timestamp, status, error, message, data);
        }
    }
}
