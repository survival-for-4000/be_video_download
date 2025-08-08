package app.video.download.global.config;

import app.video.download.global.domain.ResolutionProfile;
import app.video.download.global.error.ErrorCode;
import app.video.download.global.exception.CustomException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class ResolutionProfileDeserializer extends JsonDeserializer<ResolutionProfile> {
    
    @Override
    public ResolutionProfile deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getText();
        
        try {
            // Try direct enum name first
            return ResolutionProfile.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Try parsing as "ratio:quality" format (e.g., "16:9:HD", "1:1:SD")
            if (value.contains(":")) {
                String[] parts = value.split(":");
                if (parts.length >= 2) {
                    String ratio = parts[0] + ":" + parts[1];
                    boolean isHD = parts.length > 2 && "HD".equalsIgnoreCase(parts[2]);
                    return ResolutionProfile.from(ratio, isHD);
                }
            }
            
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }
    }
}