package com.example.response;

public class ApiResponse {
    private final long timestamp = System.currentTimeMillis();
    private int status;
    private String error;
    private String message;
    private Object data;

    public long getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
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
}
