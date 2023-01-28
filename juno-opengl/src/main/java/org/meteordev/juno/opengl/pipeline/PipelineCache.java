package org.meteordev.juno.opengl.pipeline;

import org.meteordev.juno.api.pipeline.Pipeline;
import org.meteordev.juno.api.pipeline.PipelineInfo;

import java.util.HashMap;
import java.util.Map;

public class PipelineCache {
    private final Map<PipelineInfo, Pipeline> cache = new HashMap<>();

    public Pipeline find(PipelineInfo info) {
        // Validate
        info.validate();

        // Check cache
        Pipeline pipeline = cache.get(info);
        if (pipeline != null) return pipeline;

        // Create new pipeline
        pipeline = new GLPipeline(info.copy());
        cache.put(pipeline.getInfo(), pipeline);

        return pipeline;
    }
}
