package edu.java.updates;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@AllArgsConstructor
public class UpdatesInfo {

    private boolean isSomethingUpdated;
    private OffsetDateTime updatedAt;
    private String message;
}
