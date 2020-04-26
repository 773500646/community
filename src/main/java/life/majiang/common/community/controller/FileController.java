package life.majiang.common.community.controller;

import life.majiang.common.community.dto.FileDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@Controller
public class FileController {

    @Value("${upload.url}")
    private String uploadUrl;

    @ResponseBody
    @RequestMapping("/file/upload")
    public FileDTO upload (HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("editormd-image-file");

        String defineFileDir = this.getClass().getResource("/static/upload/").getPath();
        // 获取文件名
        String fileName = file.getOriginalFilename();
        System.out.println("上传的文件名为：" + fileName);

        // 获取文件的后缀名，比如图片的jpeg,png
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        System.out.println("获取文件的后缀名：" + suffixName);

        String fileDir = defineFileDir + fileName;
        File dest = new File(fileDir);
        System.out.println(fileDir);

        FileDTO fileDTO = new FileDTO();

        try{
            file.transferTo(dest);
            fileDTO.setSuccess(1);
            fileDTO.setUrl(uploadUrl + fileName);
            return fileDTO;
        } catch (IllegalStateException e) {
            fileDTO.setSuccess(0);
            fileDTO.setMessage(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            fileDTO.setSuccess(0);
            fileDTO.setMessage(e.getMessage());
            e.printStackTrace();
        }

        return fileDTO;
    }

}
