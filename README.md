# mvnrepack
## what is this？
一个maven打包插件，可以将编译好的文件的文件名打包为`filename？m=md5`字符转的形式. 
打包后可以将引用该文件的地方替换为引用打包好的新文件
## quickstart
首先引入插件
```java
<build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>resources-repack</artifactId>
                <version>1.0-SNAPSHOT</version>
                <executions>
                    <execution>
                        <phase>
                            <!-- 仅支持编译阶段,否则报错 -->
                            compile
                        </phase>
                        <goals>
                            <goal>repack</goal>
                        </goals>
                        <configuration>
                            <resources>
                                <resource>
                                    <!-- 打包的文件类型或者contentType必填其一,也可以都填 -->
                                    <fileType>js</fileType>
                                    <contentType>text/javascript</contentType>
                                    <!--项目相对路径地址,默认不填则扫描项目路径下所有文件-->
                                    <filePath>/js<filePath>
                                    <!-- 哪些地方需要处理打包文件的文件链接,路径格式为项目的绝对路径,如/repack.js等, 默认不填则不处理, 建议仅处理jsp文件夹的链接，其余内容由前端打包工具进行处理 -->
                                    <replaceLinkFilePaths>
                                       <replaceLinkFilePath>/js</replaceLinkFilePath>
                                    </replaceLinkFilePaths>
                                </resource>
                            </resources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

运行 mvn clean install 运行结果：
[INFO] process resource js filePath null
[INFO] md5
[INFO] repack fileMap :
[INFO] {/js/repack.js=/js/repack.js?m=57165343D7761164}

