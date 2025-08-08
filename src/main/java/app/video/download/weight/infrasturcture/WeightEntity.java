package app.video.download.weight.infrasturcture;

import app.video.download.weight.domain.MediaType;
import app.video.download.weight.domain.ModelType;
import app.video.download.weight.domain.StyleType;
import app.video.download.weight.domain.Weight;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "weights")
public class WeightEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Enumerated(EnumType.STRING)
    private StyleType styleType;

    @Enumerated(EnumType.STRING)
    private ModelType modelType;

    private String image;

    private String modelName;

    private String triggerWord;

    @Column(columnDefinition = "TEXT")
    private String prompt;

    public Weight toModel() {
        return Weight.builder()
                .id(id)
                .name(name)
                .mediaType(mediaType)
                .styleType(styleType)
                .modelType(modelType)
                .image(image)
                .modelName(modelName)
                .build();
    }

    public static WeightEntity from(Weight weight) {
        WeightEntity weightEntity = new WeightEntity();
        weightEntity.id = weight.getId();
        weightEntity.name = weight.getName();
        weightEntity.mediaType = weight.getMediaType();
        weightEntity.styleType = weight.getStyleType();
        weightEntity.modelType = weight.getModelType();
        weightEntity.image = weight.getImage();
        weightEntity.modelName = weight.getModelName();
        weightEntity.triggerWord = weight.getTriggerWord();
        weightEntity.prompt = weight.getPrompt();
        return weightEntity;
    }
}
