package com.dsa.team.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 파일 업로드, 다운로드, 삭제
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FileManager {

    /**
     * 파일을 저장하고 저장된 파일명을 리턴한다.
     *
     * @param path 폴더의 절대경로
     * @param file 저장할 파일 정보
     * @return 저장된 파일명
     * @throws IOException 파일 저장 중 발생한 예외
     */
    public String saveFile(String path, MultipartFile file) throws IOException {

        // 디렉토리가 없으면 생성
        File directoryPath = new File(path);
        if (!directoryPath.isDirectory()) {
            directoryPath.mkdirs();
        }

        // ==== 서버에 저장할 파일명 생성 ====
        // 파일의 원래 이름
        String originalFileName = file.getOriginalFilename();
        // 원래 이름의 확장자 추출
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        // 오늘 날짜를 문자열로 변환
        String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // UUID 생성
        String uuidString = UUID.randomUUID().toString();
        // 저장할 파일명 생성
        String fileName = dateString + "_" + uuidString + extension;

        // 파일 복사하여 저장
        File filePath = new File(directoryPath + "/" + fileName);
        file.transferTo(filePath);

        // 파일 저장 정보를 디버그 로그에 기록
        log.debug("**파일 정보 : 원래 이름: {}, 저장된 이름: {}, 크기: {} bytes", file.getOriginalFilename(), fileName, file.getSize());
        return fileName;
    }

    /**
     * 지정된 경로와 파일명으로 디스크에서 파일을 삭제합니다.
     * 
     * @param path 파일이 위치한 폴더의 절대경로
     * @param fileName 삭제할 파일명
     * @return 파일 삭제 성공 여부 (파일이 성공적으로 삭제되면 true, 파일이 존재하지 않거나 삭제 실패 시 false)
     * @throws Exception 파일 삭제 중 발생한 예외
     */
    public boolean deleteFile(String path, String fileName) throws Exception {
        // 경로와 파일명을 결합하여 파일의 전체 경로를 생성합니다.
        Path filePath = Paths.get(path, fileName);
        
        // 파일이 존재하면 삭제하고, 삭제 성공 여부를 반환합니다.
        // 파일이 존재하지 않으면 아무 작업도 하지 않고 false를 반환합니다.
        return Files.deleteIfExists(filePath);
    }
}