package it.serravalle.cameras.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/streams")
public class MediaController {

	private static final Logger logger = LogManager.getLogger(MediaController.class);
	private static final int BUFFER_SIZE = 4096;

	private final WebClient mediaWebClient;

	@Autowired
	public MediaController(@Qualifier("mediaWebClient") WebClient mediaWebClient) {
		this.mediaWebClient = mediaWebClient;
	}

	@GetMapping("/{camera}/")
	@ResponseBody
	public StreamingResponseBody getStream(@PathVariable("camera") String camera) {
		logger.info("Call get Stream {}", camera);
		return stream(camera, "");
	}

	@GetMapping("/{camera}/{media:.+}")
	@ResponseBody
	public StreamingResponseBody stream(@PathVariable("camera") String camera, @PathVariable("media") String media) {
		logger.info("Call stream {} and media {}", camera, media);

		return outputStream -> {
			try {
				Flux<DataBuffer> dataBufferFlux = mediaWebClient.get()
						.uri("/" + camera + "/" + media)
						.retrieve()
						.bodyToFlux(DataBuffer.class);

				dataBufferFlux
						.doOnNext(dataBuffer -> {
							try {
								byte[] bytes = new byte[dataBuffer.readableByteCount()];
								dataBuffer.read(bytes);
								outputStream.write(bytes);
								outputStream.flush();
							} catch (Exception e) {
								logger.error("Error writing to output stream: {}", e.getMessage());
							} finally {
								DataBufferUtils.release(dataBuffer);
							}
						})
						.doOnError(error -> logger.error("Stream error: {}", error.getMessage()))
						.doOnComplete(() -> {
							try {
								outputStream.flush();
							} catch (Exception e) {
								logger.error("Error flushing output stream: {}", e.getMessage());
							}
						})
						.blockLast();
			} catch (Exception e) {
				logger.error("Error streaming media: {}", e.getMessage());
				throw new RuntimeException("Stream error", e);
			}
		};
	}

}
