package org.apache.maven.plugins;

import java.util.List;

/**
 * 资源对象
 */
public class Resource {

    private String fileType; // 文件类型
    private String filePath; // 文件路径
    private String contentType;
    private String repackStage; // 打包方式
    private List<String> replaceLinkFilePaths; // 文件链接替换地址
    private String encoding;

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getRepackStage() {
        return repackStage;
    }

    public void setRepackStage(String repackStage) {
        this.repackStage = repackStage;
    }

    public List<String> getReplaceLinkFilePaths() {
        return replaceLinkFilePaths;
    }

    public void setReplaceLinkFilePaths(List<String> replaceLinkFilePaths) {
        this.replaceLinkFilePaths = replaceLinkFilePaths;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
