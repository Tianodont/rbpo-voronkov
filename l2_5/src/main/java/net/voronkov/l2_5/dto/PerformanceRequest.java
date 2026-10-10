package net.voronkov.l2_5.dto;

import java.time.LocalDateTime;
import net.voronkov.l2_5.model.PerformanceStatus;

public class PerformanceRequest {

    private Long playId;
    private Long hallId;
    private LocalDateTime start;
    private Integer price;
    private PerformanceStatus status;

    public Long getPlayId() {
        return playId;
    }

    public void setPlayId(Long playId) {
        this.playId = playId;
    }

    public Long getHallId() {
        return hallId;
    }

    public void setHallId(Long hallId) {
        this.hallId = hallId;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public PerformanceStatus getStatus() {
        return status;
    }

    public void setStatus(PerformanceStatus status) {
        this.status = status;
    }
}
