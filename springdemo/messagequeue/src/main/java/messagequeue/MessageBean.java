package messagequeue;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageBean implements Serializable {
    private final Double data = Math.random();
}
