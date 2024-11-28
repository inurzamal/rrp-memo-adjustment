package com.nur.domain.id;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class RatingActionId implements Serializable {

    @Serial
    private static final long serialVersionUID = -6999009838634507722L;

    private String country;
    private LocalDate ratingDate;

}
