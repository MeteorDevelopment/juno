package org.meteordev.juno.mc.pipeline;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.meteordev.juno.api.pipeline.Pipeline;
import org.meteordev.juno.api.pipeline.PipelineInfo;

import java.util.Map;

public class PipelineCache {
    private final Map<PipelineInfo, Pipeline> cache = new Object2ObjectOpenHashMap<>();

    public Pipeline find(PipelineInfo info) {
        // Validate
        info.validate();

        // Check cache
        Pipeline pipeline = cache.get(info);
        if (pipeline != null) return pipeline;

        // Create new pipeline
        pipeline = new MCPipeline(info.copy());
        cache.put(pipeline.getInfo(), pipeline);

        return pipeline;
    }
}
