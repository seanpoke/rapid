package com.ruyuan.rapid.core.helper;


import java.util.Objects;

import com.ruyuan.rapid.common.constants.BasicConst;
import com.ruyuan.rapid.common.enums.ResponseCode;
import com.ruyuan.rapid.common.util.TimeUtil;
import com.ruyuan.rapid.core.context.Context;
import com.ruyuan.rapid.core.context.RapidResponse;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * <B>主类名称：</B>ResponseHelper<BR>
 * <B>概要说明：</B>响应的辅助类<BR>
 * @author JiFeng
 * @since 2021年12月8日 下午9:53:15
 */
public class ResponseHelper {

	/**
	 * <B>方法名称：</B>getHttpResponse<BR>
	 * <B>概要说明：</B>获取响应对象<BR>
	 * @author JiFeng
	 * @since 2021年12月8日 下午10:01:43
	 * @param responseCode
	 * @return FullHttpResponse
	 */
	public static FullHttpResponse getHttpResponse(ResponseCode responseCode) {
		RapidResponse resRapidResponse = RapidResponse.buildRapidResponse(responseCode);
		DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, 
				HttpResponseStatus.INTERNAL_SERVER_ERROR,
				Unpooled.wrappedBuffer(resRapidResponse.getContent().getBytes()));
		
		httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
		httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
		return httpResponse;
	}
	
	/**
	 * <B>方法名称：</B>getHttpResponse<BR>
	 * <B>概要说明：</B>通过上下文对象和RapidResponse对象 构建FullHttpResponse<BR>
	 * @author JiFeng
	 * @since 2021年12月20日 下午6:49:30
	 * @param ctx
	 * @param rapidResponse
	 * @return FullHttpResponse
	 */
	private static FullHttpResponse getHttpResponse(Context ctx, RapidResponse rapidResponse) {
		ByteBuf content;
		if(Objects.nonNull(rapidResponse.getFutureResponse())) {
			content = Unpooled.wrappedBuffer(rapidResponse.getFutureResponse()
					.getResponseBodyAsByteBuffer());
		}
		else if(rapidResponse.getContent() != null) {
			content = Unpooled.wrappedBuffer(rapidResponse.getContent().getBytes());
		}
		else {
			content = Unpooled.wrappedBuffer(BasicConst.BLANK_SEPARATOR_1.getBytes());
		}
		
		if(Objects.isNull(rapidResponse.getFutureResponse())) {
			DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, 
					rapidResponse.getHttpResponseStatus(),
					 content);	
			httpResponse.headers().add(rapidResponse.getResponseHeaders());
			httpResponse.headers().add(rapidResponse.getExtraResponseHeaders());
			httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
			return httpResponse;
		} else {
			rapidResponse.getFutureResponse().getHeaders().add(rapidResponse.getExtraResponseHeaders());
			
			DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, 
					 HttpResponseStatus.valueOf(rapidResponse.getFutureResponse().getStatusCode()),
					 content);	
			httpResponse.headers().add(rapidResponse.getFutureResponse().getHeaders());
			return httpResponse;
		}
	}
	

	/**
	 * <B>方法名称：</B>writeResponse<BR>
	 * <B>概要说明：</B>写回响应信息方法<BR>
	 * @author JiFeng
	 * @since 2021年12月20日 下午6:31:26
	 * @param ctx
	 */
	public static void writeResponse(Context rapidContext) {
		
		//	设置SS:
		rapidContext.setSSTime(TimeUtil.currentTimeMillis());
		
		//	释放资源
		rapidContext.releaseRequest();
		
		if(rapidContext.isWrittened()) {
			//	1：第一步构建响应对象，并写回数据
			FullHttpResponse httpResponse = ResponseHelper.getHttpResponse(rapidContext, (RapidResponse)rapidContext.getResponse());
			if(!rapidContext.isKeepAlive()) {
				rapidContext.getNettyCtx()
					.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
			} 
			//	长连接：
			else {
				httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
				rapidContext.getNettyCtx().writeAndFlush(httpResponse);
			}
			//	2:	设置写回结束状态为： COMPLETED
			rapidContext.completed();
		}
		else if(rapidContext.isCompleted()){
			rapidContext.invokeCompletedCallback();
		}
		else;
		
	}
	
}
