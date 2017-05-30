package com.huai.socket;

import static org.jboss.netty.channel.Channels.*; 
import org.jboss.netty.channel.ChannelPipeline; 
import org.jboss.netty.channel.ChannelPipelineFactory; 
import org.jboss.netty.handler.codec.http.HttpChunkAggregator; 
import org.jboss.netty.handler.codec.http.HttpRequestDecoder; 
import org.jboss.netty.handler.codec.http.HttpResponseEncoder; 

/** 
*/ 
public class WebSocketServerPipelineFactory implements ChannelPipelineFactory { 
    public ChannelPipeline getPipeline() throws Exception { 
        // Create a default pipeline implementation. 
        //创建默认的管道实现pipeline 
        ChannelPipeline pipeline = pipeline(); 
        pipeline.addLast("decoder", new HttpRequestDecoder()); 
        pipeline.addLast("aggregator", new HttpChunkAggregator(65536)); 
        pipeline.addLast("encoder", new HttpResponseEncoder()); 
        pipeline.addLast("handler", new WebSocketServerHandler()); 
        return pipeline; 
    } 
} 
