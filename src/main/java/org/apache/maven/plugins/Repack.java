package org.apache.maven.plugins;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * repack resources
 */
@Mojo(name = "repack", defaultPhase = LifecyclePhase.COMPILE)
public class Repack extends AbstractMojo {

    @Parameter(property = "resources")
    private List<Resource> resources;

    @Parameter( defaultValue = "${mojoExecution.lifecyclePhase}", readonly = true )
    private String lifecyclePhase;

    @Parameter(defaultValue = "${project.build.outputDirectory}", readonly = true)
    private String projectOutputDirectory;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // this plugin must build complile
        if (!LifecyclePhase.COMPILE.id().equals(lifecyclePhase)) {
            throw new MojoFailureException(String.valueOf(new StringBuilder("unsupport phase: plugin only support compile but found ").append(lifecyclePhase)));
        }

        // repackResource
        repackResources();
    }

    // 重新打包资源list
    private void repackResources() throws MojoExecutionException {
        if (resources == null || resources.isEmpty()) {
            return;
        }
        for (Resource resource : resources) {
            this.getLog().info(new StringBuilder("process resource : ").append(resource.getFileType()).append(" filePath :").append(resource.getFilePath()));
            repackResource(resource);
        }
    }

    // 重新打包资源
    private void repackResource(Resource resource) throws MojoExecutionException {
        Map<String, String> replaceMap = new HashMap<>();
        // 必须指定一个处理文件类型
        if ((resource.getFileType() == null || resource.getFileType().isBlank()) && (resource.getContentType() == null || resource.getContentType().isBlank())){
            return;
        }
        // 如果不指定文件处理范围,默认读取全部文件
        if (resource.getFilePath() == null || resource.getFilePath().isBlank()) {
            resource.setFilePath(projectOutputDirectory);
        } else {
            resource.setFilePath(StringUtils.join(projectOutputDirectory, resource.getFilePath()));
        }
        // 默认重新打包方式为md5
        if (resource.getRepackStage() == null || resource.getRepackStage().isBlank()) {
            resource.setRepackStage("md5");
        } else if (!"md5".equalsIgnoreCase(resource.getRepackStage())) {
            this.getLog().info(" now, this plugin only support md5, more repackStage is developing!");
            throw new MojoExecutionException("unsupport repackStage!");
        }

        // 处理文件
        File file = new File(resource.getFilePath());
        repackFile(file, resource, replaceMap);
        this.getLog().info("repack fileMap :");
        this.getLog().info(replaceMap.toString());

        // 替换链接
        replaceResources(resource, replaceMap);
    }

    // 替换资源链接
    private void replaceResources(Resource resource, Map<String, String> replaceMap) throws MojoExecutionException {
        if (resource.getReplaceLinkFilePaths()==null || resource.getReplaceLinkFilePaths().isEmpty()) {
            return;
        }
        if (replaceMap.isEmpty()) {
            return;
        }
        for (String replacePath : resource.getReplaceLinkFilePaths()) {
            File file = new File(StringUtils.join(projectOutputDirectory, replacePath));
            try {
                // 替换文件内容中的链接
                repaceFileLink(file, replaceMap);
            } catch (Exception e) {
                throw new MojoExecutionException(e);
            }
        }
    }

    // 替换文件内容中的链接
    private void repaceFileLink(File file, Map<String, String> replaceMap) throws IOException {
        if (file.isDirectory()) {
            if (file.listFiles().length > 0) {
                for (File childFile : file.listFiles()) {
                    repaceFileLink(childFile, replaceMap);
                }
            }
            return;
        }
        FileUtil.replaceContent(file, replaceMap);
    }

    // 打包处理文件
    private void repackFile(File file, Resource resource, Map<String, String> replaceMap) throws MojoExecutionException {
        if (file.isDirectory()) {
            if (file.listFiles().length > 0) {
                for (File childFile : file.listFiles()) {
                    repackFile(childFile, resource, replaceMap);
                }
            }
            return;
        }

        Path path = Paths.get(file.getAbsolutePath());
        try {
            String contentType = null;
            String fileType = null;
            if (resource.getContentType()!=null) {
                contentType = Files.probeContentType(path);
            }
            if (resource.getFileType()!=null) {
                fileType = StringUtils.substringAfterLast(file.getAbsolutePath(), ".");
            }
            if (!(contentType!=null && contentType.contains(resource.getContentType()))
            && !(fileType!=null && fileType.contains(resource.getFileType()))) {
                return;
            }
            File dest = null;
            getLog().info(resource.getRepackStage());
            if ("md5".equalsIgnoreCase(resource.getRepackStage())) {
                String md5FileName = FileUtil.getFileMD5String(file);
                dest = new File(StringUtils.join(file.getAbsolutePath(), "?m=", md5FileName, ".", StringUtils.substringAfterLast(file.getAbsolutePath(), ".")));
                replaceMap.put(StringUtils.substringAfter(file.getAbsolutePath(), projectOutputDirectory), StringUtils.substringAfter(dest.getAbsolutePath(), projectOutputDirectory));
                file.renameTo(dest);
            }
        } catch (Exception e) {
            throw new MojoExecutionException(e);
        }

    }

}
