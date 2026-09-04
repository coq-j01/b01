package kr.or.oti.b01;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.oti.b01.util.S3Uploader;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class S3UploadTest {
	@Autowired
	private S3Uploader s3Uploader;
	
//	@Test
//	public void testUpload() {
//		try {
//			String uploadName = s3Uploader.upload("c:\\upload\\apple1.png");
//			log.info("S3에 업로드된 결과 파일 이름 = "+uploadName);
//		}catch(Exception e){
//			log.error(e.getMessage());
//		}
//	}
//	
//	@Test
//	public void testRemove() {
//		try {
//			s3Uploader.removeS3File("apple1.png");
//		}catch(Exception e){
//			log.error(e.getMessage());
//		}
//	}
}
