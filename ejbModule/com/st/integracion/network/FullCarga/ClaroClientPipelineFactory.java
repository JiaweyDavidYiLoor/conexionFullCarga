package com.st.integracion.network.FullCarga;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslContext;

public class ClaroClientPipelineFactory extends ChannelInitializer<SocketChannel> {
	
	private final SslContext sslCtx;
	 
    public ClaroClientPipelineFactory(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }
    
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
    	// TODO Auto-generated method stub
    	ChannelPipeline pipeline = ch.pipeline();
    	if (sslCtx != null) {
    		pipeline.addLast(sslCtx.newHandler(ch.alloc()));
    		}
    	pipeline.addLast(new HttpClientCodec());
    	//pipeline.addLast("idleStateHandler", new IdleStateHandler(10 * 3, 15 * 3, 20 * 3));
    	//pipeline.addLast("decoder", new HttpResponseDecoder());
    	pipeline.addLast(new HttpObjectAggregator(1048576));
    	//pipeline.addLast("encoder", new HttpRequestEncoder());
    	
    	pipeline.addLast(new ClaroChannelHandler());
	
}
	
}
