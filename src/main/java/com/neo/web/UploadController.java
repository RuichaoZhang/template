package com.neo.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.neo.config.Const;
import com.neo.entity.Periodical;
import com.neo.entity.User;
import com.neo.json.AjaxJson;
import com.neo.repository.PeriodicalRepository;
import com.neo.service.AttachmentService;
import com.neo.service.PeriodicalService;
import com.neo.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "UploadController", description = "稿件操作接口")
@CrossOrigin
@RestController
@RequestMapping("/upload")
public class UploadController {

	@Autowired 
	public AttachmentService attachmentService;
	
	@Autowired
	public PeriodicalService periodicalService;
	
	@Autowired
	public PeriodicalRepository periodicalRepository;
	
	@Autowired
	public UserService userService;
	
    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "E://temp//";

    
    @ApiOperation(value="上传附件", notes="上传附件")
	@ResponseBody
    @PostMapping("/uploadPeriodical") // //new annotation since 4.3
    public AjaxJson uploadPeriodical(@RequestParam("file") MultipartFile file,
                                   	HttpServletRequest request) {
    	String token = request.getHeader("token");
    	AjaxJson j = new AjaxJson();
		if (file.isEmpty()) {
        	j.setSuccess(Const.FALSE);
        	j.setMessage(Const.UPLOAD_ERROR);
        	return j;
        }
        try {
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);
            String filePath = UPLOADED_FOLDER + path.getFileName().toString();
            j.setSuccess(Const.TRUE);
            j.setMessage(Const.UPLOAD_SUCCESS);
            Periodical periodical = new Periodical();
            periodical.setFilePath(filePath);
            j.setObject(periodical);
        } catch (IOException e) {
        	j.setSuccess(Const.FALSE);
        	j.setMessage(Const.UPLOAD_ERROR);
        }
        return j;
    }
    
    @ApiOperation(value="新增稿件", notes="新增稿件")
	@ResponseBody
    @PostMapping("/savePeriodical")
    public AjaxJson savePeriodical(@RequestBody Periodical periodical) {
    	AjaxJson j = new AjaxJson();
    	periodicalService.save(periodical);
		j.setSuccess(Const.TRUE);
    	j.setMessage("新增成功");
        return j;
    }
    
    @ApiOperation(value="根据审稿人姓名查询", notes="传入审稿人(专家)姓名")
    @RequestMapping(value = "/findPeriodicalByExpertUserName/{expertUserName}", method = RequestMethod.GET)
	@ResponseBody
	public Object findPeriodicalByExpertUserName(HttpServletRequest request,
			@PathVariable("expertUserName") String expertUserName) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> page = new HashMap<>();
		
		page.put("number", 0);
		page.put("size", 0);
		page.put("totalElements", 0);
		page.put("totalPages", 0);
		map.put("page", page);
		Map<String, List> data = new HashMap<>();
		User user = userService.findUserByToken(request.getHeader("token"));
		AjaxJson j = new AjaxJson();
		String userId = String.valueOf(user.getId());
		if(userId!=null && !"".equals(userId)) {
			data.put("periodicals", periodicalRepository.findByExpertUserName(expertUserName));
			map.put("_embedded", data);
			j.setSuccess(true);
			j.setMessage("查询成功");
		}else {
			j.setSuccess(false);
		}
		
		return map;
	}

    
    @ApiOperation(value="根据发布人姓名查询", notes="传入发布人姓名")
    @RequestMapping(value = "/findPeriodicalPublishUserName/{publishUserName}", method = RequestMethod.GET)
	@ResponseBody
	public Object findPeriodicalPublishUserName(HttpServletRequest request,
			@PathVariable("publishUserName") String publishUserName) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> page = new HashMap<>();
		
		page.put("number", 0);
		page.put("size", 0);
		page.put("totalElements", 0);
		page.put("totalPages", 0);
		map.put("page", page);
		Map<String, List> data = new HashMap<>();
		User user = userService.findUserByToken(request.getHeader("token"));
		AjaxJson j = new AjaxJson();
		String userId = String.valueOf(user.getId());
		if(userId!=null && !"".equals(userId)) {
			data.put("periodicals", periodicalRepository.findByPublishUserName(publishUserName));
			map.put("_embedded", data);
			j.setSuccess(true);
			j.setMessage("查询成功");
		}else {
			j.setSuccess(false);
		}
		
		return map;
	}

    
    @ApiOperation(value="查询全部", notes="查询全部")
    @RequestMapping(value = "/findAllPeriodical", method = RequestMethod.GET)
	@ResponseBody
	public Object findAllPeriodical(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> page = new HashMap<>();
		
		page.put("number", 0);
		page.put("size", 0);
		page.put("totalElements", 0);
		page.put("totalPages", 0);
		map.put("page", page);
		Map<String, List> data = new HashMap<>();
		User user = userService.findUserByToken(request.getHeader("token"));
		AjaxJson j = new AjaxJson();
		String userId = String.valueOf(user.getId());
		if(userId!=null && !"".equals(userId)) {
			data.put("periodicals", (List) periodicalRepository.findAll());
			map.put("_embedded", data);
			j.setSuccess(true);
			j.setMessage("查询成功");
		}else {
			j.setSuccess(false);
		}
		
		return map;
	}
  
    
    /**
     * 下载文件
     * @return
     */
    @GetMapping("/downloadPeriodical/{id}")
    public AjaxJson downloadPeriodical(
    		@PathVariable("id") String id,
    		HttpServletResponse res) {
    	AjaxJson j = new AjaxJson();
		
    	File file = new File (periodicalRepository.findOne(Long.parseLong(id)).getFilePath());
    	res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
          os = res.getOutputStream();
          bis = new BufferedInputStream(new FileInputStream(file));
          int i = bis.read(buff);
          while (i != -1) {
            os.write(buff, 0, buff.length);
            os.flush();
            i = bis.read(buff);
          }
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          if (bis != null) {
            try {
              bis.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
        j.setSuccess(Const.TRUE);
    	j.setMessage("下载成功");
        return j;
    }
    
  
}