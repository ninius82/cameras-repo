package it.serravalle.cameras.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import it.serravalle.cameras.config.MediaClient;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@EnableAsync
@RequestMapping("/streams")
public class MediaController {
	
	private static Logger logger = LogManager.getLogger(MetadataController.class);
	
	@Autowired
	private MediaClient mediaClient;
			
//	@RequestMapping("/")
//	public String index() {
//		logger.info("Call index");
//		return "index";
//	}


	@GetMapping("/{camera}/")
	@ResponseBody
	public StreamingResponseBody getStream(@PathVariable("camera") String camera) 
			throws IOException {	
		try {
			logger.info("Call get Stream {}", camera);
			return stream (camera, "");
		} catch (Exception e) {
			logger.error("Error {}", e.getMessage());
			// return null;
			return new ResponseEntity<StreamingResponseBody>(HttpStatus.BAD_REQUEST).getBody();
		}

	}


	@GetMapping("/{camera}/{media:.+}")
	@ResponseBody
	public StreamingResponseBody stream(@PathVariable("camera") String camera, @PathVariable("media") String media)
			throws IOException {
		try {

			
			Flux<DataBuffer> body = mediaClient.getClient().get()
								.uri("/" + camera + "/" + media)
								.retrieve()
								.bodyToFlux(DataBuffer.class);
			InputStream st = getInputStreamFromFluxDataBuffer(body);

			logger.info("Call stream {} and media {}", camera, media);
			return (os) -> {
				readAndWrite(st, os);
			};
		} catch (Exception e) {
			logger.error("Error {}", e.getMessage());
			// return null;
			return new ResponseEntity<StreamingResponseBody>(HttpStatus.BAD_REQUEST).getBody();
		}

	}

	private InputStream getInputStreamFromFluxDataBuffer(Flux<DataBuffer> data) throws IOException {
	    PipedOutputStream osPipe = new PipedOutputStream();
	    PipedInputStream isPipe = new PipedInputStream(osPipe);

	    DataBufferUtils.write(data, osPipe)
	    		.subscribeOn(Schedulers.boundedElastic())
	            .doOnComplete(() -> {
	                try {
	                    osPipe.close();
	                } catch (IOException ignored) {
	                }
	            })
	            .subscribe(DataBufferUtils.releaseConsumer());
	    return isPipe;
	}
	
	private void readAndWrite(final InputStream is, OutputStream os) throws IOException {
		byte[] data = new byte[2048];
		int read = 0;
		while ((read = is.read(data)) > 0) {
			os.write(data, 0, read);
		}
		os.flush();
	}

}
