# Backend - Code Source

G�n�r� le 2026-07-13 11:48

---

## .idea\compiler.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="CompilerConfiguration">
    <option name="PARALLEL_COMPILATION_OPTION" value="Automatic" />
    <annotationProcessing>
      <profile default="true" name="Default" enabled="true" />
      <profile name="Maven default annotation processors profile" enabled="true">
        <sourceOutputDir name="target/generated-sources/annotations" />
        <sourceTestOutputDir name="target/generated-test-sources/test-annotations" />
        <outputRelativeToContentRoot value="true" />
      </profile>
      <profile name="Annotation profile for ng-fields-backend" enabled="true">
        <sourceOutputDir name="target/generated-sources/annotations" />
        <sourceTestOutputDir name="target/generated-test-sources/test-annotations" />
        <outputRelativeToContentRoot value="true" />
        <processorPath useClasspath="false">
          <entry name="$MAVEN_REPOSITORY$/org/projectlombok/lombok/1.18.46/lombok-1.18.46.jar" />
        </processorPath>
        <module name="intervention-service" />
        <module name="auth-service" />
        <module name="gateway-service" />
        <module name="media-service" />
        <module name="report-service" />
        <module name="notification-service" />
        <module name="client-service" />
      </profile>
    </annotationProcessing>
  </component>
  <component name="JavacSettings">
    <option name="ADDITIONAL_OPTIONS_OVERRIDE">
      <module name="auth-service" options="-parameters" />
      <module name="client-service" options="-parameters" />
      <module name="gateway-service" options="-parameters" />
      <module name="intervention-service" options="-parameters" />
      <module name="media-service" options="-parameters" />
      <module name="notification-service" options="-parameters" />
      <module name="report-service" options="-parameters" />
    </option>
  </component>
</project>
`` 


## .idea\encodings.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="Encoding">
    <file url="file://$PROJECT_DIR$/auth-service/src/main/java" charset="UTF-8" />
    <file url="file://$PROJECT_DIR$/client-service/src/main/java" charset="UTF-8" />
    <file url="file://$PROJECT_DIR$/gateway-service/src/main/java" charset="UTF-8" />
    <file url="file://$PROJECT_DIR$/intervention-service/src/main/java" charset="UTF-8" />
    <file url="file://$PROJECT_DIR$/media-service/src/main/java" charset="UTF-8" />
    <file url="file://$PROJECT_DIR$/notification-service/src/main/java" charset="UTF-8" />
    <file url="file://$PROJECT_DIR$/report-service/src/main/java" charset="UTF-8" />
  </component>
</project>
`` 


## .idea\jarRepositories.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="RemoteRepositoriesConfiguration">
    <remote-repository>
      <option name="id" value="central" />
      <option name="name" value="Central Repository" />
      <option name="url" value="https://repo.maven.apache.org/maven2" />
    </remote-repository>
    <remote-repository>
      <option name="id" value="central" />
      <option name="name" value="Maven Central repository" />
      <option name="url" value="https://repo1.maven.org/maven2" />
    </remote-repository>
    <remote-repository>
      <option name="id" value="jboss.community" />
      <option name="name" value="JBoss Community repository" />
      <option name="url" value="https://repository.jboss.org/nexus/content/repositories/public/" />
    </remote-repository>
  </component>
</project>
`` 


## .idea\misc.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ExternalStorageConfigurationManager" enabled="true" />
  <component name="MavenProjectsManager">
    <option name="originalFiles">
      <list>
        <option value="$PROJECT_DIR$/pom.xml" />
      </list>
    </option>
  </component>
  <component name="ProjectRootManager" version="2" project-jdk-name="25" project-jdk-type="JavaSDK" />
</project>
`` 


## .idea\vcs.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="VcsDirectoryMappings">
    <mapping directory="$PROJECT_DIR$/../../.." vcs="Git" />
  </component>
</project>
`` 


## .idea\workspace.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="AutoImportSettings">
    <option name="autoReloadType" value="SELECTIVE" />
  </component>
  <component name="ChangeListManager">
    <list default="true" id="2c00d4da-c153-4337-a559-a3a2361b65c4" name="Changes" comment="">
      <change beforePath="$PROJECT_DIR$/../../../.gitignore" beforeDir="false" afterPath="$PROJECT_DIR$/../../../.gitignore" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/.idea/compiler.xml" beforeDir="false" afterPath="$PROJECT_DIR$/.idea/compiler.xml" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/auth-service/.env" beforeDir="false" />
      <change beforePath="$PROJECT_DIR$/auth-service/pom.xml" beforeDir="false" afterPath="$PROJECT_DIR$/auth-service/pom.xml" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/auth-service/src/main/java/tg/ngstars/auth/config/GlobalExceptionHandler.java" beforeDir="false" afterPath="$PROJECT_DIR$/auth-service/src/main/java/tg/ngstars/auth/config/GlobalExceptionHandler.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/auth-service/src/main/java/tg/ngstars/auth/config/SecurityConfig.java" beforeDir="false" afterPath="$PROJECT_DIR$/auth-service/src/main/java/tg/ngstars/auth/config/SecurityConfig.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/auth-service/src/main/java/tg/ngstars/auth/dto/CreateUserRequest.java" beforeDir="false" afterPath="$PROJECT_DIR$/auth-service/src/main/java/tg/ngstars/auth/dto/CreateUserRequest.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/auth-service/src/main/java/tg/ngstars/auth/model/AuditLog.java" beforeDir="false" afterPath="$PROJECT_DIR$/auth-service/src/main/java/tg/ngstars/auth/model/AuditLog.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/auth-service/src/main/java/tg/ngstars/auth/model/User.java" beforeDir="false" afterPath="$PROJECT_DIR$/auth-service/src/main/java/tg/ngstars/auth/model/User.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/auth-service/src/main/java/tg/ngstars/auth/service/AuditService.java" beforeDir="false" afterPath="$PROJECT_DIR$/auth-service/src/main/java/tg/ngstars/auth/service/AuditService.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/auth-service/src/main/java/tg/ngstars/auth/service/UserService.java" beforeDir="false" afterPath="$PROJECT_DIR$/auth-service/src/main/java/tg/ngstars/auth/service/UserService.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/auth-service/src/main/resources/application.yml" beforeDir="false" afterPath="$PROJECT_DIR$/auth-service/src/main/resources/application.yml" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/auth-service/src/test/java/tg/ngstars/auth/service/UserServiceTest.java" beforeDir="false" afterPath="$PROJECT_DIR$/auth-service/src/test/java/tg/ngstars/auth/service/UserServiceTest.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/client-service/.env" beforeDir="false" />
      <change beforePath="$PROJECT_DIR$/client-service/src/main/java/tg/ngstars/client/config/SecurityConfig.java" beforeDir="false" afterPath="$PROJECT_DIR$/client-service/src/main/java/tg/ngstars/client/config/SecurityConfig.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/client-service/src/main/java/tg/ngstars/client/model/Client.java" beforeDir="false" afterPath="$PROJECT_DIR$/client-service/src/main/java/tg/ngstars/client/model/Client.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/client-service/src/main/java/tg/ngstars/client/repository/ClientRepository.java" beforeDir="false" afterPath="$PROJECT_DIR$/client-service/src/main/java/tg/ngstars/client/repository/ClientRepository.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/client-service/src/main/java/tg/ngstars/client/service/ClientService.java" beforeDir="false" afterPath="$PROJECT_DIR$/client-service/src/main/java/tg/ngstars/client/service/ClientService.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/client-service/src/main/resources/application.yml" beforeDir="false" afterPath="$PROJECT_DIR$/client-service/src/main/resources/application.yml" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/client-service/src/test/java/tg/ngstars/client/service/ClientServiceTest.java" beforeDir="false" afterPath="$PROJECT_DIR$/client-service/src/test/java/tg/ngstars/client/service/ClientServiceTest.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/gateway-service/.env" beforeDir="false" />
      <change beforePath="$PROJECT_DIR$/gateway-service/pom.xml" beforeDir="false" afterPath="$PROJECT_DIR$/gateway-service/pom.xml" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/gateway-service/src/main/java/tg/ngstars/gateway/config/RateLimitConfig.java" beforeDir="false" afterPath="$PROJECT_DIR$/gateway-service/src/main/java/tg/ngstars/gateway/config/RateLimitConfig.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/gateway-service/src/main/java/tg/ngstars/gateway/config/SecurityConfig.java" beforeDir="false" afterPath="$PROJECT_DIR$/gateway-service/src/main/java/tg/ngstars/gateway/config/SecurityConfig.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/gateway-service/src/main/resources/application.yml" beforeDir="false" afterPath="$PROJECT_DIR$/gateway-service/src/main/resources/application.yml" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/.env" beforeDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/pom.xml" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/pom.xml" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/client/MediaClient.java" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/client/MediaClient.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/config/GlobalExceptionHandler.java" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/config/GlobalExceptionHandler.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/config/SecurityConfig.java" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/config/SecurityConfig.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/controller/InterventionController.java" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/controller/InterventionController.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/controller/SignatureController.java" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/controller/SignatureController.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/controller/SyncController.java" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/controller/SyncController.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/dto/UpdateBillingRequest.java" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/dto/UpdateBillingRequest.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/dto/UpdateDiagnosisRequest.java" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/dto/UpdateDiagnosisRequest.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/dto/UpdateEquipmentRequest.java" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/dto/UpdateEquipmentRequest.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/dto/UpdateRecommendationsRequest.java" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/dto/UpdateRecommendationsRequest.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/dto/UpdateScheduleRequest.java" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/dto/UpdateScheduleRequest.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/model/Intervention.java" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/model/Intervention.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/model/InterventionItem.java" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/model/InterventionItem.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/model/InterventionPhoto.java" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/model/InterventionPhoto.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/repository/InterventionRepository.java" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/repository/InterventionRepository.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/service/InterventionService.java" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/service/InterventionService.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/service/PhotoService.java" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/service/PhotoService.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/service/SecurityUtils.java" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/service/SecurityUtils.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/service/SignatureService.java" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/main/java/tg/ngstars/interv/service/SignatureService.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/main/resources/application.yml" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/main/resources/application.yml" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/intervention-service/src/test/java/tg/ngstars/interv/service/InterventionServiceTest.java" beforeDir="false" afterPath="$PROJECT_DIR$/intervention-service/src/test/java/tg/ngstars/interv/service/InterventionServiceTest.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/media-service/.env" beforeDir="false" />
      <change beforePath="$PROJECT_DIR$/media-service/pom.xml" beforeDir="false" afterPath="$PROJECT_DIR$/media-service/pom.xml" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/media-service/src/main/java/tg/ngstars/media/config/GlobalExceptionHandler.java" beforeDir="false" afterPath="$PROJECT_DIR$/media-service/src/main/java/tg/ngstars/media/config/GlobalExceptionHandler.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/media-service/src/main/java/tg/ngstars/media/config/SecurityConfig.java" beforeDir="false" afterPath="$PROJECT_DIR$/media-service/src/main/java/tg/ngstars/media/config/SecurityConfig.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/media-service/src/main/java/tg/ngstars/media/controller/FileController.java" beforeDir="false" afterPath="$PROJECT_DIR$/media-service/src/main/java/tg/ngstars/media/controller/FileController.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/media-service/src/main/java/tg/ngstars/media/service/FileService.java" beforeDir="false" afterPath="$PROJECT_DIR$/media-service/src/main/java/tg/ngstars/media/service/FileService.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/media-service/src/main/resources/application.yml" beforeDir="false" afterPath="$PROJECT_DIR$/media-service/src/main/resources/application.yml" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/notification-service/pom.xml" beforeDir="false" afterPath="$PROJECT_DIR$/notification-service/pom.xml" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/notification-service/src/main/java/tg/ngstars/notification_service/NotificationServiceApplication.java" beforeDir="false" afterPath="$PROJECT_DIR$/notification-service/src/main/java/tg/ngstars/notification/NotificationServiceApplication.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/notification-service/src/main/resources/application.yaml" beforeDir="false" />
      <change beforePath="$PROJECT_DIR$/notification-service/src/test/java/tg/ngstars/notification_service/NotificationServiceApplicationTests.java" beforeDir="false" />
      <change beforePath="$PROJECT_DIR$/pom.xml" beforeDir="false" afterPath="$PROJECT_DIR$/pom.xml" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/report-service/pom.xml" beforeDir="false" afterPath="$PROJECT_DIR$/report-service/pom.xml" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/report-service/src/main/java/tg/ngstars/report_service/ReportServiceApplication.java" beforeDir="false" afterPath="$PROJECT_DIR$/report-service/src/main/java/tg/ngstars/report/ReportServiceApplication.java" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/report-service/src/main/resources/application.yaml" beforeDir="false" />
      <change beforePath="$PROJECT_DIR$/report-service/src/test/java/tg/ngstars/report_service/ReportServiceApplicationTests.java" beforeDir="false" />
      <change beforePath="$PROJECT_DIR$/../auth/.env" beforeDir="false" />
      <change beforePath="$PROJECT_DIR$/../doc/CLAUDE.md" beforeDir="false" afterPath="$PROJECT_DIR$/../doc/CLAUDE.md" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/../doc/README.md" beforeDir="false" afterPath="$PROJECT_DIR$/../doc/README.md" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/../doc/docs/architecture/stack-technique.md" beforeDir="false" afterPath="$PROJECT_DIR$/../doc/docs/architecture/stack-technique.md" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/../doc/infra/docker-compose.override.yml" beforeDir="false" afterPath="$PROJECT_DIR$/../doc/infra/docker-compose.override.yml" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/../doc/infra/docker-compose.yml" beforeDir="false" afterPath="$PROJECT_DIR$/../doc/infra/docker-compose.yml" afterDir="false" />
      <change beforePath="$PROJECT_DIR$/../doc/infra/keycloak/realm-export.json" beforeDir="false" afterPath="$PROJECT_DIR$/../doc/infra/keycloak/realm-export.json" afterDir="false" />
    </list>
    <option name="SHOW_DIALOG" value="false" />
    <option name="HIGHLIGHT_CONFLICTS" value="true" />
    <option name="HIGHLIGHT_NON_ACTIVE_CHANGELIST" value="false" />
    <option name="LAST_RESOLUTION" value="IGNORE" />
  </component>
  <component name="CompilerWorkspaceConfiguration">
    <option name="MAKE_PROJECT_ON_SAVE" value="true" />
  </component>
  <component name="Git.Settings">
    <option name="RECENT_GIT_ROOT_PATH" value="$PROJECT_DIR$/../../.." />
  </component>
  <component name="MavenImportPreferences">
    <option name="generalSettings">
      <MavenGeneralSettings>
        <option name="mavenHomeTypeForPersistence" value="WRAPPER" />
      </MavenGeneralSettings>
    </option>
  </component>
  <component name="NextEditCompletionFeaturesState">
    <decayedCancelled>
      <entry key="MS100" value="1.0840702101770412" />
      <entry key="MS500" value="2.301408429325438" />
      <entry key="S2" value="3.421741067945789" />
      <entry key="S5" value="4.040329477715526" />
      <entry key="S10" value="4.84867127948952" />
      <entry key="S30" value="6.39889480362134" />
      <entry key="S60" value="7.207795842487231" />
      <entry key="M2" value="7.864929029500599" />
      <entry key="M5" value="8.457097466238114" />
      <entry key="M10" value="8.709762678766644" />
      <entry key="M15" value="8.801971209161032" />
      <entry key="M30" value="8.898618465337009" />
      <entry key="H1" value="8.94869818053568" />
      <entry key="H2" value="8.974193846578249" />
      <entry key="H4" value="8.987057797652739" />
      <entry key="D1" value="8.997837505229263" />
      <entry key="W1" value="8.999690938083056" />
    </decayedCancelled>
    <decayedSelected>
      <entry key="MS100" value="4.015520769619131E-117" />
      <entry key="MS500" value="5.2571258696375375E-24" />
      <entry key="S2" value="1.5142134302743005E-6" />
      <entry key="S5" value="0.004699750876334427" />
      <entry key="S10" value="0.0685547290588653" />
      <entry key="S30" value="0.40927241385021096" />
      <entry key="S60" value="0.6397440221293285" />
      <entry key="M2" value="0.7998399978303965" />
      <entry key="M5" value="0.914536929663458" />
      <entry key="M10" value="0.9563142421105405" />
      <entry key="M15" value="0.9706598867096196" />
      <entry key="M30" value="0.9852207299431023" />
      <entry key="H1" value="0.9925828579736318" />
      <entry key="H2" value="0.9962845266155806" />
      <entry key="H4" value="0.9981405345018208" />
      <entry key="D1" value="0.999689848698488" />
      <entry key="W1" value="0.9999556867806632" />
    </decayedSelected>
    <decayedShown>
      <entry key="MS100" value="0.9496202714628973" />
      <entry key="MS500" value="1.9044060916959438" />
      <entry key="S2" value="3.16832449824133" />
      <entry key="S5" value="3.795691097029031" />
      <entry key="S10" value="4.546140027310369" />
      <entry key="S30" value="6.42514171593883" />
      <entry key="S60" value="7.568506030056614" />
      <entry key="M2" value="8.486861218084565" />
      <entry key="M5" value="9.28622997008352" />
      <entry key="M10" value="9.620304572054815" />
      <entry key="M15" value="9.741366959749454" />
      <entry key="M30" value="9.86781389974944" />
      <entry key="H1" value="9.933166402873765" />
      <entry key="H2" value="9.966395080686338" />
      <entry key="H4" value="9.983150131377233" />
      <entry key="D1" value="9.997185071562502" />
      <entry key="W1" value="9.999597704892459" />
    </decayedShown>
  </component>
  <component name="ProblemsViewState">
    <option name="selectedTabId" value="CurrentFile" />
    <option name="showPreview" value="true" />
  </component>
  <component name="ProjectColorInfo">{
  &quot;associatedIndex&quot;: 6,
  &quot;fromUser&quot;: false
}</component>
  <component name="ProjectId" id="3FuCHGZs6AGXk83aEf2s8hpMUHs" />
  <component name="ProjectViewState">
    <option name="hideEmptyMiddlePackages" value="true" />
    <option name="showLibraryContents" value="true" />
  </component>
  <component name="PropertiesComponent"><![CDATA[{
  "keyToString": {
    "ModuleVcsDetector.initialDetectionPerformed": "true",
    "RunOnceActivity.ShowReadmeOnStart": "true",
    "RunOnceActivity.TerminalTabsStorage.copyFrom.TerminalArrangementManager.252": "true",
    "RunOnceActivity.git.unshallow": "true",
    "RunOnceActivity.typescript.service.memoryLimit.init": "true",
    "SHELLCHECK.PATH": "C:\\Users\\FOLLY\\AppData\\Roaming\\JetBrains\\IntelliJIdea2026.1\\plugins\\Shell Script\\shellcheck.exe",
    "Spring Boot.AuthServiceApplication.executor": "Run",
    "Spring Boot.ClientServiceApplication.executor": "Run",
    "Spring Boot.Gateway.executor": "Run",
    "Spring Boot.GatewayServiceApplication.executor": "Run",
    "Spring Boot.Inter.executor": "Run",
    "Spring Boot.InterventionServiceApplication.executor": "Run",
    "Spring Boot.Media.executor": "Run",
    "Spring Boot.Notif.executor": "Run",
    "Spring Boot.Report.executor": "Run",
    "Spring Boot.auth.executor": "Run",
    "Spring Boot.client.executor": "Run",
    "codeWithMe.voiceChat.enabledByDefault": "false",
    "com.intellij.ml.llm.matterhorn.ej.ui.settings.DefaultModelSelectionForGA.v1": "true",
    "git-widget-placeholder": "main",
    "ignore.virus.scanning.warn.message": "true",
    "junie.onboarding.icon.badge.shown": "true",
    "kotlin-language-version-configured": "true",
    "last_opened_file_path": "F:/03_Pro_IT/07_Clients/NG-STARs/06_PROJETS/ng-fields/Backend",
    "node.js.detected.package.eslint": "true",
    "node.js.detected.package.tslint": "true",
    "node.js.selected.package.eslint": "(autodetect)",
    "node.js.selected.package.tslint": "(autodetect)",
    "nodejs_package_manager_path": "npm",
    "project.structure.last.edited": "Global Libraries",
    "project.structure.proportion": "0.0",
    "project.structure.side.proportion": "0.7468355",
    "run.code.analysis.last.selected.profile": "pProject Default",
    "run.configurations.included.in.services": "true",
    "settings.editor.selected.configurable": "project.propCompiler",
    "to.speed.mode.migration.done": "true",
    "vue.rearranger.settings.migration": "true"
  }
}]]></component>
  <component name="RunDashboard">
    <option name="configurationTypes">
      <set>
        <option value="KtorApplicationConfigurationType" />
        <option value="SpringBootApplicationConfigurationType" />
      </set>
    </option>
    <option name="configurationStatuses">
      <map>
        <entry key="SpringBootApplicationConfigurationType">
          <value>
            <map>
              <entry key="Gateway" value="FAILED" />
              <entry key="Inter" value="FAILED" />
              <entry key="Media" value="FAILED" />
              <entry key="Notif" value="STOPPED" />
              <entry key="Report" value="STOPPED" />
              <entry key="auth" value="FAILED" />
              <entry key="client" value="FAILED" />
            </map>
          </value>
        </entry>
      </map>
    </option>
  </component>
  <component name="RunManager" selected="Spring Boot.auth">
    <configuration name="Gateway" type="SpringBootApplicationConfigurationType" factoryName="Spring Boot">
      <module name="gateway-service" />
      <option name="SPRING_BOOT_MAIN_CLASS" value="tg.ngstars.gateway.GatewayServiceApplication" />
      <method v="2">
        <option name="Make" enabled="true" />
      </method>
    </configuration>
    <configuration name="Inter" type="SpringBootApplicationConfigurationType" factoryName="Spring Boot">
      <module name="intervention-service" />
      <option name="SPRING_BOOT_MAIN_CLASS" value="tg.ngstars.interv.InterventionServiceApplication" />
      <method v="2">
        <option name="Make" enabled="true" />
      </method>
    </configuration>
    <configuration name="Media" type="SpringBootApplicationConfigurationType" factoryName="Spring Boot">
      <module name="media-service" />
      <option name="SPRING_BOOT_MAIN_CLASS" value="tg.ngstars.media.MediaServiceApplication" />
      <method v="2">
        <option name="Make" enabled="true" />
      </method>
    </configuration>
    <configuration name="Notif" type="SpringBootApplicationConfigurationType" factoryName="Spring Boot">
      <module name="notification-service" />
      <option name="SPRING_BOOT_MAIN_CLASS" value="tg.ngstars.notification.NotificationServiceApplication" />
      <method v="2">
        <option name="Make" enabled="true" />
      </method>
    </configuration>
    <configuration name="Report" type="SpringBootApplicationConfigurationType" factoryName="Spring Boot">
      <module name="report-service" />
      <option name="SPRING_BOOT_MAIN_CLASS" value="tg.ngstars.report.ReportServiceApplication" />
      <method v="2">
        <option name="Make" enabled="true" />
      </method>
    </configuration>
    <configuration name="auth" type="SpringBootApplicationConfigurationType" factoryName="Spring Boot">
      <option name="ALTERNATIVE_JRE_PATH" value="C:/Program Files/Java/jdk-25.0.2" />
      <option name="ALTERNATIVE_JRE_PATH_ENABLED" value="true" />
      <module name="auth-service" />
      <option name="SPRING_BOOT_MAIN_CLASS" value="tg.ngstars.auth.AuthServiceApplication" />
      <method v="2">
        <option name="Make" enabled="true" />
      </method>
    </configuration>
    <configuration name="client" type="SpringBootApplicationConfigurationType" factoryName="Spring Boot">
      <module name="client-service" />
      <option name="SPRING_BOOT_MAIN_CLASS" value="tg.ngstars.client.ClientServiceApplication" />
      <method v="2">
        <option name="Make" enabled="true" />
      </method>
    </configuration>
    <list>
      <item itemvalue="Spring Boot.Gateway" />
      <item itemvalue="Spring Boot.auth" />
      <item itemvalue="Spring Boot.client" />
      <item itemvalue="Spring Boot.Inter" />
      <item itemvalue="Spring Boot.Media" />
      <item itemvalue="Spring Boot.Notif" />
      <item itemvalue="Spring Boot.Report" />
    </list>
  </component>
  <component name="SharedIndexes">
    <attachedChunks>
      <set>
        <option value="bundled-jdk-30f59d01ecdd-37e91769500f-intellij.indexing.shared.core-IU-261.26222.65" />
        <option value="bundled-js-predefined-d6986cc7102b-31caf2ab9e3c-JavaScript-IU-261.26222.65" />
      </set>
    </attachedChunks>
  </component>
  <component name="TaskManager">
    <task active="true" id="Default" summary="Default task">
      <changelist id="2c00d4da-c153-4337-a559-a3a2361b65c4" name="Changes" comment="" />
      <created>1782915502174</created>
      <option name="number" value="Default" />
      <option name="presentableId" value="Default" />
      <updated>1782915502174</updated>
      <workItem from="1782988271273" duration="13000" />
      <workItem from="1782988391393" duration="1866000" />
      <workItem from="1783012534450" duration="6479000" />
      <workItem from="1783079862852" duration="633000" />
    </task>
    <servers />
  </component>
  <component name="TypeScriptGeneratedFilesManager">
    <option name="version" value="3" />
  </component>
</project>
`` 


## auth-service\.idea\misc.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="MavenProjectsManager">
    <option name="originalFiles">
      <list>
        <option value="$PROJECT_DIR$/pom.xml" />
      </list>
    </option>
  </component>
  <component name="ProjectRootManager" version="2" languageLevel="JDK_25" default="true" project-jdk-name="25" project-jdk-type="JavaSDK">
    <output url="file://$PROJECT_DIR$/target" />
  </component>
</project>

`` 


## auth-service\.idea\modules.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectModuleManager">
    <modules>
      <module fileurl="file://$PROJECT_DIR$/.idea/auth-service.iml" filepath="$PROJECT_DIR$/.idea/auth-service.iml" />
    </modules>
  </component>
</project>

`` 


## auth-service\.idea\vcs.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="VcsDirectoryMappings">
    <mapping directory="$PROJECT_DIR$/../../.." vcs="Git" />
  </component>
</project>

`` 


## auth-service\src\main\java\tg\ngstars\auth\config\GlobalExceptionHandler.java

``java
package tg.ngstars.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tg.ngstars.auth.exception.ConflictException;
import tg.ngstars.auth.exception.NotFoundException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                fe -> fe.getField(),
                fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalide"
            ));
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Erreur de validation");
        detail.setProperty("errors", errors);
        return detail;
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        detail.setDetail(ex.getMessage());
        return detail;
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        detail.setDetail(ex.getMessage());
        return detail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        detail.setDetail("Acces refuse : droits insuffisants");
        return detail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex) {
        log.error("Unexpected error", ex);
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        detail.setTitle("Erreur interne");
        detail.setDetail("Une erreur inattendue s'est produite");
        return detail;
    }
}

`` 


## auth-service\src\main\java\tg\ngstars\auth\config\KeycloakAdminConfig.java

``java
package tg.ngstars.auth.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminConfig {

    @Bean
    public Keycloak keycloak(KeycloakProperties props) {
        return KeycloakBuilder.builder()
                .serverUrl(props.authServerUrl())
                .realm(props.realm())
                .clientId(props.adminClientId())
                .clientSecret(props.adminClientSecret())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}

`` 


## auth-service\src\main\java\tg\ngstars\auth\config\KeycloakProperties.java

``java
package tg.ngstars.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak")
public record KeycloakProperties(
    String authServerUrl,
    String adminClientId,
    String adminClientSecret,
    String realm
) {}

`` 


## auth-service\src\main\java\tg\ngstars\auth\config\SecurityConfig.java

``java
package tg.ngstars.auth.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origins:http://localhost:4200,http://localhost:8100}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new RealmRoleConverter());
        return converter;
    }

    // ponytail: duplicated across 6 services, extract to shared lib if modules ever split
    static class RealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        @SuppressWarnings("unchecked")
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null) return List.of();
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            if (roles == null) return List.of();
            return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        }
    }
}

`` 


## auth-service\src\main\java\tg\ngstars\auth\controller\HealthController.java

``java
package tg.ngstars.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/public/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}

`` 


## auth-service\src\main\java\tg\ngstars\auth\controller\UserController.java

``java
package tg.ngstars.auth.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tg.ngstars.auth.dto.CreateUserRequest;
import tg.ngstars.auth.dto.RoleAssignRequest;
import tg.ngstars.auth.dto.UpdateProfileRequest;
import tg.ngstars.auth.dto.UserResponse;
import tg.ngstars.auth.dto.UserStatusRequest;
import tg.ngstars.auth.service.UserService;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(request, jwt.getSubject()));
    }

    @GetMapping("/api/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/api/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PutMapping("/api/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody CreateUserRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userService.updateUser(id, request, jwt.getSubject()));
    }

    @DeleteMapping("/api/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        userService.deleteUser(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/admin/users/{keycloakId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> assignRole(
            @PathVariable UUID keycloakId,
            @Valid @RequestBody RoleAssignRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                userService.assignRole(keycloakId, request.role(), jwt.getSubject()));
    }

    @PatchMapping("/api/admin/users/{keycloakId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateStatus(
            @PathVariable UUID keycloakId,
            @RequestBody UserStatusRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                userService.updateUserStatus(keycloakId, request.enabled(), jwt.getSubject()));
    }

    @PostMapping("/api/admin/users/{keycloakId}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> resetPassword(
            @PathVariable UUID keycloakId,
            @AuthenticationPrincipal Jwt jwt) {
        userService.sendPasswordReset(keycloakId, jwt.getSubject());
        return ResponseEntity.ok(Map.of("message", "Email de reinitialisation envoye"));
    }

    @GetMapping("/api/users/me")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userService.getProfile(UUID.fromString(jwt.getSubject())));
    }

    @PutMapping("/api/users/me")
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                userService.updateProfile(UUID.fromString(jwt.getSubject()), request));
    }

    @PostMapping("/api/public/register")
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestBody CreateUserRequest request,
            HttpServletRequest httpRequest) {
        var created = userService.registerClient(request, clientIp(httpRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Compte cree. Vous pouvez vous connecter sur le portail client.",
                "user", created));
    }

    private static String clientIp(HttpServletRequest request) {
        var xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank())
            return xff.split(",")[0].trim();
        return request.getRemoteAddr();
    }
}

`` 


## auth-service\src\main\java\tg\ngstars\auth\dto\CreateUserRequest.java

``java
package tg.ngstars.auth.dto;

import jakarta.validation.constraints.*;

public record CreateUserRequest(
    @NotBlank @Size(min = 3, max = 50)
    String username,

    @NotBlank @Email
    String email,

    @NotBlank @Size(max = 100)
    String firstName,

    @NotBlank @Size(max = 100)
    String lastName,

    @NotBlank @Size(min = 6)
    String password,

    @NotBlank
    @Pattern(regexp = "ADMIN|MANAGER|TECHNICIAN|CLIENT_PORTAL",
             message = "Role invalide : ADMIN, MANAGER, TECHNICIAN, CLIENT_PORTAL")
    String role,

    String phone
) {}

`` 


## auth-service\src\main\java\tg\ngstars\auth\dto\RoleAssignRequest.java

``java
package tg.ngstars.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RoleAssignRequest(
    @NotBlank
    @Pattern(regexp = "ADMIN|MANAGER|TECHNICIAN|CLIENT_PORTAL")
    String role
) {}

`` 


## auth-service\src\main\java\tg\ngstars\auth\dto\UpdateProfileRequest.java

``java
package tg.ngstars.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
    @NotBlank @Size(max = 100) String firstName,
    @NotBlank @Size(max = 100) String lastName
) {}

`` 


## auth-service\src\main\java\tg\ngstars\auth\dto\UserResponse.java

``java
package tg.ngstars.auth.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponse(
    UUID id,
    UUID keycloakId,
    String username,
    String email,
    String firstName,
    String lastName,
    String role,
    String phone,
    boolean active,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}

`` 


## auth-service\src\main\java\tg\ngstars\auth\dto\UserStatusRequest.java

``java
package tg.ngstars.auth.dto;

public record UserStatusRequest(boolean enabled) {}

`` 


## auth-service\src\main\java\tg\ngstars\auth\exception\ConflictException.java

``java
package tg.ngstars.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {
    public ConflictException(String message) { super(message); }
}

`` 


## auth-service\src\main\java\tg\ngstars\auth\exception\NotFoundException.java

``java
package tg.ngstars.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}

`` 


## auth-service\src\main\java\tg\ngstars\auth\model\AuditLog.java

``java
package tg.ngstars.auth.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;


@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    private String action;

    private String resource;

    @Column(name = "resource_id")
    private String resourceId;

    private String details;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }

    public AuditLog() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}

`` 


## auth-service\src\main\java\tg\ngstars\auth\model\User.java

``java
package tg.ngstars.auth.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

@Entity
@Table(name = "users",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "keycloak_id"),
            @UniqueConstraint(columnNames = "username"),
            @UniqueConstraint(columnNames = "email")
       })
public class User {

    @Id
    private UUID id;

    @Column(name = "keycloak_id", nullable = false, unique = true)
    private UUID keycloakId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String role;

    private String phone;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        var now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() { updatedAt = OffsetDateTime.now(); }

    public User() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getKeycloakId() { return keycloakId; }
    public void setKeycloakId(UUID keycloakId) { this.keycloakId = keycloakId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}

`` 


## auth-service\src\main\java\tg\ngstars\auth\repository\AuditLogRepository.java

``java
package tg.ngstars.auth.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import tg.ngstars.auth.model.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}

`` 


## auth-service\src\main\java\tg\ngstars\auth\repository\UserRepository.java

``java
package tg.ngstars.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import tg.ngstars.auth.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByKeycloakId(UUID keycloakId);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}

`` 


## auth-service\src\main\java\tg\ngstars\auth\service\AuditService.java

``java
package tg.ngstars.auth.service;

import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tg.ngstars.auth.model.AuditLog;
import tg.ngstars.auth.repository.AuditLogRepository;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(UUID userId, String action, String resource, String resourceId, String details, String ipAddress) {
        if (userId == null) {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                try { userId = UUID.fromString(jwt.getSubject()); } catch (IllegalArgumentException ignored) {}
            }
        }
        var log = new AuditLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setResource(resource);
        log.setResourceId(resourceId);
        log.setDetails(details);
        log.setIpAddress(ipAddress);
        auditLogRepository.save(log);
    }
}

`` 


## auth-service\src\main\java\tg\ngstars\auth\service\SecurityUtils.java

``java
package tg.ngstars.auth.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public UUID getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Jwt jwt)
                || jwt.getSubject() == null)
            return null;
        try {
            return UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public List<String> getCurrentUserRoles() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return List.of();
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.replace("ROLE_", ""))
                .toList();
    }
}

`` 


## auth-service\src\main\java\tg\ngstars\auth\service\UserService.java

``java
package tg.ngstars.auth.service;

import java.util.List;
import java.util.UUID;

import jakarta.ws.rs.core.Response;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tg.ngstars.auth.config.KeycloakProperties;
import tg.ngstars.auth.dto.CreateUserRequest;
import tg.ngstars.auth.dto.UpdateProfileRequest;
import tg.ngstars.auth.dto.UserResponse;
import tg.ngstars.auth.exception.ConflictException;
import tg.ngstars.auth.exception.NotFoundException;
import tg.ngstars.auth.model.User;
import tg.ngstars.auth.repository.UserRepository;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final AuditService auditService;
    private final Keycloak keycloak;
    private final KeycloakProperties keycloakProperties;

    public UserService(UserRepository userRepository, AuditService auditService,
            Keycloak keycloak, KeycloakProperties keycloakProperties) {
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.keycloak = keycloak;
        this.keycloakProperties = keycloakProperties;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request, String createdBy) {
        UUID createdById = userIdOrNull(createdBy);
        if (userRepository.existsByUsername(request.username()))
            throw new ConflictException("Username '" + request.username() + "' deja utilise");
        if (userRepository.existsByEmail(request.email()))
            throw new ConflictException("Email '" + request.email() + "' deja utilise");

        var kcUser = new UserRepresentation();
        kcUser.setUsername(request.username());
        kcUser.setEmail(request.email());
        kcUser.setFirstName(request.firstName());
        kcUser.setLastName(request.lastName());
        kcUser.setEnabled(true);
        if (request.password() != null)
            kcUser.setCredentials(List.of(passwordCredential(request.password())));

        var realm = keycloak.realm(keycloakProperties.realm());
        UUID keycloakId = null;
        try (Response response = realm.users().create(kcUser)) {
            if (response.getStatus() != 201)
                throw new RuntimeException("Echec creation Keycloak: " + response.getStatus());

            var location = response.getLocation();
            keycloakId = UUID.fromString(location.getPath().substring(location.getPath().lastIndexOf('/') + 1));

            if (request.role() != null)
                assignRealmRole(keycloakId.toString(), request.role());

            var user = new User();
            user.setKeycloakId(keycloakId);
            user.setUsername(request.username());
            user.setEmail(request.email());
            user.setFirstName(request.firstName());
            user.setLastName(request.lastName());
            user.setRole(request.role());
            user.setPhone(request.phone());
            user.setActive(true);
            userRepository.save(user);

            auditService.log(createdById, "USER_CREATED", "User", user.getId().toString(),
                    "Compte cree: " + request.username(), null);
            log.info("Compte cree: {} (keycloakId={})", request.username(), keycloakId);

            return toResponse(user);
        } catch (RuntimeException e) {
            if (keycloakId != null) {
                try {
                    realm.users().get(keycloakId.toString()).remove();
                    log.warn("Keycloak user {} cleaned up after DB failure", keycloakId);
                } catch (Exception ignored) {
                    log.warn("Failed to cleanup Keycloak user {}", keycloakId, ignored);
                }
            }
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(UUID id) {
        return toResponse(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + id)));
    }

    @Transactional
    public UserResponse updateUser(UUID id, CreateUserRequest request, String updatedBy) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + id));

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setRole(request.role());
        user.setPhone(request.phone());

        var kcIdStr = user.getKeycloakId().toString();
        var kcUser = keycloak.realm(keycloakProperties.realm()).users().get(kcIdStr).toRepresentation();
        kcUser.setUsername(request.username());
        kcUser.setEmail(request.email());
        kcUser.setFirstName(request.firstName());
        kcUser.setLastName(request.lastName());
        keycloak.realm(keycloakProperties.realm()).users().get(kcIdStr).update(kcUser);

        if (request.password() != null)
            keycloak.realm(keycloakProperties.realm()).users().get(kcIdStr)
                    .resetPassword(passwordCredential(request.password()));
        if (request.role() != null && !request.role().equals(user.getRole()))
            assignRealmRole(kcIdStr, request.role());

        auditService.log(userIdOrNull(updatedBy), "USER_UPDATED", "User", user.getId().toString(),
                "Compte mis a jour: " + user.getUsername(), null);

        return toResponse(user);
    }

    @Transactional
    public void deleteUser(UUID id, String deletedBy) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + id));
        user.setActive(false);

        var kcIdStr = user.getKeycloakId().toString();
        var kcUser = keycloak.realm(keycloakProperties.realm()).users().get(kcIdStr).toRepresentation();
        kcUser.setEnabled(false);
        keycloak.realm(keycloakProperties.realm()).users().get(kcIdStr).update(kcUser);

        auditService.log(userIdOrNull(deletedBy), "USER_DELETED", "User",
                user.getId().toString(), "Compte desactive: " + user.getEmail(), null);
    }

    @Transactional
    public UserResponse assignRole(UUID keycloakId, String newRole, String adminId) {
        var user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + keycloakId));
        user.setRole(newRole);

        var kcIdStr = keycloakId.toString();
        var realm = keycloak.realm(keycloakProperties.realm());

        var metierRoles = List.of("ADMIN", "MANAGER", "TECHNICIAN", "CLIENT_PORTAL");
        var toRemove = realm.users().get(kcIdStr).roles().realmLevel().listAll().stream()
                .filter(r -> metierRoles.contains(r.getName()))
                .toList();
        if (!toRemove.isEmpty())
            realm.users().get(kcIdStr).roles().realmLevel().remove(toRemove);

        var role = realm.roles().get(newRole).toRepresentation();
        realm.users().get(kcIdStr).roles().realmLevel().add(List.of(role));

        auditService.log(userIdOrNull(adminId), "ROLE_ASSIGNED", "User",
                user.getId().toString(), "Role " + newRole + " assigne a " + user.getUsername(), null);

        return toResponse(user);
    }

    @Transactional
    public UserResponse updateUserStatus(UUID keycloakId, boolean enabled, String adminId) {
        var user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + keycloakId));
        user.setActive(enabled);

        var kcIdStr = keycloakId.toString();
        var kcUser = keycloak.realm(keycloakProperties.realm()).users().get(kcIdStr).toRepresentation();
        kcUser.setEnabled(enabled);
        keycloak.realm(keycloakProperties.realm()).users().get(kcIdStr).update(kcUser);

        var action = enabled ? "ACCOUNT_ENABLED" : "ACCOUNT_DISABLED";
        auditService.log(userIdOrNull(adminId), action, "User",
                user.getId().toString(), "Compte " + user.getUsername() + ": " + (enabled ? "active" : "desactive"), null);

        return toResponse(user);
    }

    @Transactional
    public void sendPasswordReset(UUID keycloakId, String adminId) {
        userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + keycloakId));
        keycloak.realm(keycloakProperties.realm()).users().get(keycloakId.toString())
                .executeActionsEmail(List.of("UPDATE_PASSWORD"));
        auditService.log(userIdOrNull(adminId), "PASSWORD_RESET_SENT", "User",
                keycloakId.toString(), "Email de reinitialisation envoye", null);
        log.info("Email de reinitialisation envoye pour keycloakId={}", keycloakId);
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(UUID keycloakId) {
        return toResponse(userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("Profil introuvable")));
    }

    @Transactional
    public UserResponse updateProfile(UUID keycloakId, UpdateProfileRequest request) {
        var user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("Profil introuvable"));

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        var kcIdStr = keycloakId.toString();
        var kcUser = keycloak.realm(keycloakProperties.realm()).users().get(kcIdStr).toRepresentation();
        kcUser.setFirstName(request.firstName());
        kcUser.setLastName(request.lastName());
        keycloak.realm(keycloakProperties.realm()).users().get(kcIdStr).update(kcUser);

        return toResponse(user);
    }

    public UserResponse registerClient(CreateUserRequest request, String ip) {
        return createUser(new CreateUserRequest(
                request.username(), request.email(),
                request.firstName(), request.lastName(),
                request.password(), "CLIENT_PORTAL", request.phone()), "SELF_REGISTER");
    }

    private void assignRealmRole(String userId, String role) {
        var realm = keycloak.realm(keycloakProperties.realm());
        var roleRep = realm.roles().get(role).toRepresentation();
        realm.users().get(userId).roles().realmLevel().add(List.of(roleRep));
    }

    private static UUID userIdOrNull(String s) {
        if (s == null) return null;
        try { return UUID.fromString(s); } catch (IllegalArgumentException e) { return null; }
    }

    private static CredentialRepresentation passwordCredential(String password) {
        var cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(password);
        cred.setTemporary(false);
        return cred;
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getKeycloakId(),
                user.getUsername(), user.getEmail(),
                user.getFirstName(), user.getLastName(),
                user.getRole(), user.getPhone(),
                user.getActive(), user.getCreatedAt(), user.getUpdatedAt());
    }
}

`` 


## auth-service\src\main\java\tg\ngstars\auth\AuthServiceApplication.java

``java
package tg.ngstars.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import tg.ngstars.auth.config.KeycloakProperties;

@SpringBootApplication
@EnableConfigurationProperties(KeycloakProperties.class)
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}

`` 


## auth-service\src\main\resources\db\migration\V1__init_schema.sql

``sql
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    keycloak_id UUID NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    phone VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY,
    user_id UUID,
    action VARCHAR(100) NOT NULL,
    resource VARCHAR(100),
    resource_id VARCHAR(100),
    details TEXT,
    ip_address VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

`` 


## auth-service\src\main\resources\db\migration\V2__add_audit_logs_index.sql

``sql
CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON audit_logs(user_id);

`` 


## auth-service\src\main\resources\application-dev.yml

``yml
spring:
  jpa:
    show-sql: true
  devtools:
    restart:
      enabled: true

keycloak:
  auth-server-url: http://localhost:8088
  admin-client-id: admin-cli
  admin-client-secret: ${KEYCLOAK_ADMIN_CLIENT_SECRET:}

logging:
  level:
    tg.ngstars: DEBUG
    org.springframework.security: DEBUG

`` 


## auth-service\src\main\resources\application-prod.yml

``yml
springdoc:
  swagger-ui:
    enabled: false

logging:
  level:
    tg.ngstars: WARN

`` 


## auth-service\src\main\resources\application.yml

``yml
server:
  port: 8081
  shutdown: graceful

spring:
  application:
    name: auth-service
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  lifecycle:
    timeout-per-shutdown-phase: 30s
  config:
    import: optional:file:${ENV_FILE:./.env}[.properties]
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:ng_fields}?currentSchema=auth
    username: ${DB_USER:ng_fields_user}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        default_schema: auth
  flyway:
    enabled: true
    schemas: auth
    locations: classpath:db/migration
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${KEYCLOAK_ISSUER_URI:http://localhost:8088/realms/ng-fields}

app:
  cors:
    allowed-origins: ${CORS_ORIGINS:http://localhost:4200,http://localhost:8100}

keycloak:
  auth-server-url: ${KEYCLOAK_AUTH_SERVER_URL:http://localhost:8088}
  admin-client-id: ${KEYCLOAK_ADMIN_CLIENT_ID:ng-fields-backend}
  admin-client-secret: ${KEYCLOAK_ADMIN_CLIENT_SECRET:}
  realm: ${KEYCLOAK_REALM:ng-fields}

springdoc:
  swagger-ui:
    path: /swagger-ui.html

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
`` 


## auth-service\src\test\java\tg\ngstars\auth\service\AuditServiceTest.java

``java
package tg.ngstars.auth.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tg.ngstars.auth.model.AuditLog;
import tg.ngstars.auth.repository.AuditLogRepository;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock AuditLogRepository auditLogRepository;
    @InjectMocks AuditService service;

    @Test
    void log_shouldSaveAuditLog() {
        service.log(UUID.randomUUID(), "USER_CREATED", "User", "123", "details", "127.0.0.1");
        verify(auditLogRepository).save(any(AuditLog.class));
    }
}

`` 


## auth-service\src\test\java\tg\ngstars\auth\service\UserServiceTest.java

``java
package tg.ngstars.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tg.ngstars.auth.config.KeycloakProperties;
import tg.ngstars.auth.dto.CreateUserRequest;
import tg.ngstars.auth.dto.UpdateProfileRequest;
import tg.ngstars.auth.exception.ConflictException;
import tg.ngstars.auth.exception.NotFoundException;
import tg.ngstars.auth.model.User;
import tg.ngstars.auth.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock AuditService auditService;
    @Mock Keycloak keycloak;
    @Mock RealmResource realm;
    @Mock UsersResource usersResource;
    @Mock UserResource userResource;
    @Mock RolesResource rolesResource;
    @Mock RoleResource roleResource;
    @Mock RoleMappingResource roleMappingResource;
    @Mock RoleScopeResource roleScopeResource;

    UserService service;
    KeycloakProperties props = new KeycloakProperties("http://localhost:8088", "admin-cli", "secret", "ng-fields");

    UUID keycloakId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new UserService(userRepository, auditService, keycloak, props);
        lenient().when(keycloak.realm(props.realm())).thenReturn(realm);
        lenient().when(realm.users()).thenReturn(usersResource);
        lenient().when(usersResource.get(keycloakId.toString())).thenReturn(userResource);
        lenient().when(userResource.roles()).thenReturn(roleMappingResource);
        lenient().when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);
        lenient().when(realm.roles()).thenReturn(rolesResource);
    }

    private User user() {
        var u = new User();
        u.setId(userId);
        u.setKeycloakId(keycloakId);
        u.setUsername("jdoe");
        u.setEmail("j@doe.com");
        u.setFirstName("John");
        u.setLastName("Doe");
        u.setRole("TECHNICIAN");
        u.setActive(true);
        return u;
    }

    @Test
    void createUser_shouldCreateInKeycloakAndDb() throws Exception {
        var req = new CreateUserRequest("jdoe", "j@doe.com", "John", "Doe", "pass123", "TECHNICIAN", null);

        when(userRepository.existsByUsername("jdoe")).thenReturn(false);
        when(userRepository.existsByEmail("j@doe.com")).thenReturn(false);

        var locationUri = new URI("http://localhost:8088/admin/realms/ng-fields/users/" + keycloakId);
        var response = mock(Response.class);
        when(response.getStatus()).thenReturn(201);
        when(response.getLocation()).thenReturn(locationUri);
        when(usersResource.create(any())).thenReturn(response);

        when(rolesResource.get("TECHNICIAN")).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(new RoleRepresentation());

        when(userRepository.save(any())).thenAnswer(i -> {
            var u = (User) i.getArgument(0);
            u.setId(userId);
            return u;
        });

        var result = service.createUser(req, "admin");

        assertEquals("jdoe", result.username());
        assertEquals("TECHNICIAN", result.role());
        verify(auditService).log(any(), eq("USER_CREATED"), eq("User"), anyString(), anyString(), isNull());
    }

    @Test
    void createUser_duplicateUsername_throwsConflict() {
        var req = new CreateUserRequest("jdoe", "j@doe.com", "John", "Doe", "pass123", "TECHNICIAN", null);
        when(userRepository.existsByUsername("jdoe")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.createUser(req, "admin"));
    }

    @Test
    void getUser_shouldReturnUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user()));
        var result = service.getUser(userId);
        assertEquals("jdoe", result.username());
    }

    @Test
    void getUser_notFound_throwsNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getUser(userId));
    }

    @Test
    void getAllUsers_shouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(user()));
        var result = service.getAllUsers();
        assertEquals(1, result.size());
    }

    @Test
    void assignRole_shouldUpdateRole() {
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(user()));
        when(roleScopeResource.listAll()).thenReturn(List.of());
        when(rolesResource.get("MANAGER")).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(new RoleRepresentation());

        var result = service.assignRole(keycloakId, "MANAGER", "admin");
        assertEquals("MANAGER", result.role());
    }

    @Test
    void updateUserStatus_enable_shouldActivate() {
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(user()));
        when(userResource.toRepresentation()).thenReturn(new UserRepresentation());

        var result = service.updateUserStatus(keycloakId, true, "admin");

        assertTrue(result.active());
        verify(auditService).log(any(), eq("ACCOUNT_ENABLED"), eq("User"), anyString(), anyString(), isNull());
    }

    @Test
    void getProfile_shouldReturnByKeycloakId() {
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(user()));
        var result = service.getProfile(keycloakId);
        assertEquals("jdoe", result.username());
    }

    @Test
    void updateProfile_shouldUpdateNames() {
        var request = new UpdateProfileRequest("Jane", "Smith");
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(user()));
        when(userResource.toRepresentation()).thenReturn(new UserRepresentation());

        var result = service.updateProfile(keycloakId, request);

        assertEquals("Jane", result.firstName());
        assertEquals("Smith", result.lastName());
    }

    @Test
    void deleteUser_shouldDisable() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user()));
        when(userResource.toRepresentation()).thenReturn(new UserRepresentation());

        service.deleteUser(userId, "admin");

        verify(auditService).log(any(), eq("USER_DELETED"), eq("User"), anyString(), contains("desactive"), isNull());
    }

    @Test
    void registerClient_createsWithClientPortalRole() throws Exception {
        var req = new CreateUserRequest("client1", "c@test.com", "Client", "One", "pass123", "ADMIN", null);

        when(userRepository.existsByUsername("client1")).thenReturn(false);
        when(userRepository.existsByEmail("c@test.com")).thenReturn(false);

        var locationUri = new URI("http://localhost:8088/admin/realms/ng-fields/users/" + keycloakId);
        var response = mock(Response.class);
        when(response.getStatus()).thenReturn(201);
        when(response.getLocation()).thenReturn(locationUri);
        when(usersResource.create(any())).thenReturn(response);

        when(rolesResource.get("CLIENT_PORTAL")).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(new RoleRepresentation());

        when(userRepository.save(any())).thenAnswer(i -> {
            var u = (User) i.getArgument(0);
            u.setId(userId);
            return u;
        });

        var result = service.registerClient(req, "127.0.0.1");

        assertEquals("CLIENT_PORTAL", result.role());
    }
}

`` 


## client-service\.idea\misc.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="MavenProjectsManager">
    <option name="originalFiles">
      <list>
        <option value="$PROJECT_DIR$/pom.xml" />
      </list>
    </option>
  </component>
  <component name="ProjectRootManager" version="2" languageLevel="JDK_25" default="true" project-jdk-name="25" project-jdk-type="JavaSDK">
    <output url="file://$PROJECT_DIR$/target" />
  </component>
</project>

`` 


## client-service\.idea\modules.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectModuleManager">
    <modules>
      <module fileurl="file://$PROJECT_DIR$/.idea/client-service.iml" filepath="$PROJECT_DIR$/.idea/client-service.iml" />
    </modules>
  </component>
</project>

`` 


## client-service\.idea\vcs.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="VcsDirectoryMappings">
    <mapping directory="$PROJECT_DIR$/../../.." vcs="Git" />
  </component>
</project>

`` 


## client-service\src\main\java\tg\ngstars\client\config\GlobalExceptionHandler.java

``java
package tg.ngstars.client.config;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import tg.ngstars.client.exception.ConflictException;
import tg.ngstars.client.exception.NotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Bad Request");
        problem.setDetail(ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed"));
        problem.setType(URI.create("about:blank"));
        return problem;
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Conflict");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("about:blank"));
        return problem;
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Not Found");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("about:blank"));
        return problem;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problem.setTitle("Forbidden");
        problem.setDetail("Access denied");
        problem.setType(URI.create("about:blank"));
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        var problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Internal Server Error");
        problem.setDetail("An unexpected error occurred");
        problem.setType(URI.create("about:blank"));
        return problem;
    }
}

`` 


## client-service\src\main\java\tg\ngstars\client\config\SecurityConfig.java

``java
package tg.ngstars.client.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origins:http://localhost:4200,http://localhost:8100}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new RealmRoleConverter());
        return converter;
    }

    static class RealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        @SuppressWarnings("unchecked")
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null) return List.of();
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            if (roles == null) return List.of();
            return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        }
    }
}

`` 


## client-service\src\main\java\tg\ngstars\client\controller\ClientController.java

``java
package tg.ngstars.client.controller;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tg.ngstars.client.dto.ClientResponse;
import tg.ngstars.client.dto.CreateClientRequest;
import tg.ngstars.client.dto.UpdateClientRequest;
import tg.ngstars.client.service.ClientService;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientResponse> createClient(
            @Valid @RequestBody CreateClientRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clientService.createClient(request, jwt.getSubject()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public ResponseEntity<Page<ClientResponse>> listClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(clientService.listClients(page, size));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public ResponseEntity<Page<ClientResponse>> searchClients(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(clientService.searchClients(q, page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public ResponseEntity<ClientResponse> getClient(@PathVariable UUID id) {
        return ResponseEntity.ok(clientService.getClient(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientResponse> updateClient(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateClientRequest request) {
        return ResponseEntity.ok(clientService.updateClient(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateClient(@PathVariable UUID id) {
        clientService.deactivateClient(id);
        return ResponseEntity.noContent().build();
    }
}

`` 


## client-service\src\main\java\tg\ngstars\client\dto\ClientResponse.java

``java
package tg.ngstars.client.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ClientResponse(
    UUID id,
    String reference,
    String companyName,
    String contactName,
    String email,
    String phone,
    String address,
    Double latitude,
    Double longitude,
    boolean active,
    OffsetDateTime createdAt
) {}

`` 


## client-service\src\main\java\tg\ngstars\client\dto\CreateClientRequest.java

``java
package tg.ngstars.client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateClientRequest(
    @NotBlank @Size(max = 200) String companyName,
    @Size(max = 150) String contactName,
    @NotBlank @Email @Size(max = 150) String email,
    @Size(max = 30) String phone,
    String address,
    Double latitude,
    Double longitude
) {}

`` 


## client-service\src\main\java\tg\ngstars\client\dto\UpdateClientRequest.java

``java
package tg.ngstars.client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateClientRequest(
    @NotBlank @Size(max = 200) String companyName,
    @Size(max = 150) String contactName,
    @NotBlank @Email @Size(max = 150) String email,
    @Size(max = 30) String phone,
    String address,
    Double latitude,
    Double longitude
) {}

`` 


## client-service\src\main\java\tg\ngstars\client\exception\ConflictException.java

``java
package tg.ngstars.client.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {
    public ConflictException(String message) { super(message); }
}

`` 


## client-service\src\main\java\tg\ngstars\client\exception\NotFoundException.java

``java
package tg.ngstars.client.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}

`` 


## client-service\src\main\java\tg\ngstars\client\model\Client.java

``java
package tg.ngstars.client.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "clients",
       uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter @Setter @ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String reference;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "contact_name", length = 150)
    private String contactName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    private Double latitude;

    private Double longitude;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Version
    private Long version;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        var now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() { updatedAt = OffsetDateTime.now(); }
}

`` 


## client-service\src\main\java\tg\ngstars\client\repository\ClientRepository.java

``java
package tg.ngstars.client.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tg.ngstars.client.model.Client;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    boolean existsByEmail(String email);

    Optional<Client> findByReference(String reference);

    Page<Client> findByActiveTrue(Pageable pageable);

    @Query("""
        SELECT c FROM Client c
        WHERE c.active = true
        AND (
            LOWER(c.companyName) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(c.contactName) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(c.email) LIKE LOWER(CONCAT('%', :q, '%'))
        )
        """)
    Page<Client> search(@Param("q") String query, Pageable pageable);

    @Query("SELECT MAX(c.reference) FROM Client c")
    String findMaxReference();
}

`` 


## client-service\src\main\java\tg\ngstars\client\service\ClientService.java

``java
package tg.ngstars.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tg.ngstars.client.dto.ClientResponse;
import tg.ngstars.client.dto.CreateClientRequest;
import tg.ngstars.client.dto.UpdateClientRequest;
import tg.ngstars.client.exception.ConflictException;
import tg.ngstars.client.exception.NotFoundException;
import tg.ngstars.client.model.Client;
import tg.ngstars.client.repository.ClientRepository;

@Service
public class ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientService.class);
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional
    public ClientResponse createClient(CreateClientRequest request, String createdBy) {
        if (clientRepository.existsByEmail(request.email()))
            throw new ConflictException("Un client avec l'email '" + request.email() + "' existe deja");

        var client = Client.builder()
                .reference(generateReference())
                .companyName(request.companyName())
                .contactName(request.contactName())
                .email(request.email())
                .phone(request.phone())
                .address(request.address())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .createdBy(createdBy)
                .build();

        var saved = clientRepository.save(client);
        log.info("Fiche client creee : {} (ref={})", request.companyName(), saved.getReference());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ClientResponse> listClients(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("companyName").ascending());
        return clientRepository.findByActiveTrue(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ClientResponse getClient(java.util.UUID id) {
        return clientRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Client introuvable : id=" + id));
    }

    @Transactional
    public ClientResponse updateClient(java.util.UUID id, UpdateClientRequest request) {
        var client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client introuvable : id=" + id));

        if (!client.getEmail().equals(request.email()) && clientRepository.existsByEmail(request.email()))
            throw new ConflictException("L'email '" + request.email() + "' est deja utilise");

        client.setCompanyName(request.companyName());
        client.setContactName(request.contactName());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setAddress(request.address());
        client.setLatitude(request.latitude());
        client.setLongitude(request.longitude());

        return toResponse(clientRepository.save(client));
    }

    @Transactional
    public void deactivateClient(java.util.UUID id) {
        var client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client introuvable : id=" + id));
        client.setActive(false);
        clientRepository.save(client);
        log.info("Client desactive : {}", client.getCompanyName());
    }

    @Transactional(readOnly = true)
    public Page<ClientResponse> searchClients(String query, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("companyName").ascending());
        return clientRepository.search(query, pageable).map(this::toResponse);
    }

    // ponytail: synchronized, per-sequence DB table if throughput matters
    private synchronized String generateReference() {
        var maxRef = clientRepository.findMaxReference();
        var next = maxRef != null ? Integer.parseInt(maxRef.replace("CLT-", "")) + 1 : 1;
        return String.format("CLT-%04d", next);
    }

    private ClientResponse toResponse(Client c) {
        return new ClientResponse(c.getId(), c.getReference(), c.getCompanyName(),
                c.getContactName(), c.getEmail(), c.getPhone(), c.getAddress(),
                c.getLatitude(), c.getLongitude(), c.getActive(), c.getCreatedAt());
    }
}

`` 


## client-service\src\main\java\tg\ngstars\client\ClientServiceApplication.java

``java
package tg.ngstars.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClientServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientServiceApplication.class, args);
    }
}

`` 


## client-service\src\main\resources\db\migration\V1__init_schema.sql

``sql
CREATE TABLE IF NOT EXISTS clients (
    id           UUID           PRIMARY KEY,
    reference    VARCHAR(20)    NOT NULL UNIQUE,
    company_name VARCHAR(200)   NOT NULL,
    contact_name VARCHAR(150),
    email        VARCHAR(150)   NOT NULL UNIQUE,
    phone        VARCHAR(30),
    address      TEXT,
    latitude     DOUBLE PRECISION,
    longitude    DOUBLE PRECISION,
    active       BOOLEAN        NOT NULL DEFAULT TRUE,
    created_by   VARCHAR(100),
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_clients_email ON clients(email);
CREATE INDEX IF NOT EXISTS idx_clients_active ON clients(active);
CREATE INDEX IF NOT EXISTS idx_clients_company ON clients(company_name);

`` 


## client-service\src\main\resources\application-dev.yml

``yml
spring:
  jpa:
    show-sql: true

logging:
  level:
    tg.ngstars: DEBUG

`` 


## client-service\src\main\resources\application-prod.yml

``yml
springdoc:
  swagger-ui:
    enabled: false

logging:
  level:
    tg.ngstars: WARN

`` 


## client-service\src\main\resources\application.yml

``yml
server:
  port: 8082
  shutdown: graceful

spring:
  application:
    name: client-service
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  lifecycle:
    timeout-per-shutdown-phase: 30s
  config:
    import: optional:file:${ENV_FILE:./.env}[.properties]
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:ng_fields}?currentSchema=client
    username: ${DB_USER:ng_fields_user}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        default_schema: client
  flyway:
    enabled: true
    schemas: client
    locations: classpath:db/migration
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${KEYCLOAK_ISSUER_URI:http://localhost:8088/realms/ng-fields}

app:
  cors:
    allowed-origins: ${CORS_ORIGINS:http://localhost:4200,http://localhost:8100}

keycloak:
  auth-server-url: ${KEYCLOAK_AUTH_SERVER_URL:http://localhost:8088}
  admin-client-id: ${KEYCLOAK_ADMIN_CLIENT_ID:ng-fields-backend}
  admin-client-secret: ${KEYCLOAK_ADMIN_CLIENT_SECRET:}
  realm: ${KEYCLOAK_REALM:ng-fields}

springdoc:
  swagger-ui:
    path: /swagger-ui.html

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus

`` 


## client-service\src\test\java\tg\ngstars\client\service\ClientServiceTest.java

``java
package tg.ngstars.client.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import tg.ngstars.client.dto.CreateClientRequest;
import tg.ngstars.client.dto.UpdateClientRequest;
import tg.ngstars.client.exception.ConflictException;
import tg.ngstars.client.exception.NotFoundException;
import tg.ngstars.client.model.Client;
import tg.ngstars.client.repository.ClientRepository;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock ClientRepository clientRepository;
    ClientService service;

    UUID clientId = UUID.randomUUID();
    Client client;

    @BeforeEach
    void setUp() {
        service = new ClientService(clientRepository);
        client = Client.builder()
                .id(clientId)
                .reference("CLT-0001")
                .companyName("ACME Inc")
                .contactName("John")
                .email("acme@test.com")
                .phone("123456")
                .address("123 Main St")
                .latitude(48.85)
                .longitude(2.35)
                .active(true)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void createClient_shouldGenerateReference() {
        var req = new CreateClientRequest("ACME Inc", "John", "acme@test.com", "123456", "123 Main St", 48.85, 2.35);
        when(clientRepository.existsByEmail("acme@test.com")).thenReturn(false);
        when(clientRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.createClient(req, "admin");

        assertEquals("CLT-0001", result.reference());
        assertEquals("ACME Inc", result.companyName());
        assertEquals("acme@test.com", result.email());
    }

    @Test
    void createClient_duplicateEmail_throwsConflict() {
        var req = new CreateClientRequest("ACME Inc", "John", "acme@test.com", "123456", "123 Main St", 48.85, 2.35);
        when(clientRepository.existsByEmail("acme@test.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.createClient(req, "admin"));
    }

    @Test
    void getClient_shouldReturnClient() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        var result = service.getClient(clientId);
        assertEquals("ACME Inc", result.companyName());
    }

    @Test
    void getClient_notFound_throwsNotFound() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getClient(clientId));
    }

    @Test
    void listClients_shouldReturnPage() {
        var page = new PageImpl<>(java.util.List.of(client));
        when(clientRepository.findByActiveTrue(any(Pageable.class))).thenReturn(page);

        var result = service.listClients(0, 10);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void updateClient_shouldUpdateFields() {
        var req = new UpdateClientRequest("NewCo", "Jane", "new@test.com", "999", "New addr", 47.0, 1.0);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.updateClient(clientId, req);

        assertEquals("NewCo", result.companyName());
        assertEquals("Jane", result.contactName());
        assertEquals("new@test.com", result.email());
    }

    @Test
    void updateClient_duplicateEmail_throwsConflict() {
        var req = new UpdateClientRequest("NewCo", "Jane", "other@test.com", "999", "Addr", null, null);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientRepository.existsByEmail("other@test.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.updateClient(clientId, req));
    }

    @Test
    void deactivateClient_shouldSetInactive() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.deactivateClient(clientId);

        assertFalse(client.getActive());
    }

    @Test
    void deactivateClient_notFound_throwsNotFound() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.deactivateClient(clientId));
    }

    @Test
    void searchClients_shouldReturnResults() {
        var page = new PageImpl<>(java.util.List.of(client));
        when(clientRepository.search(eq("ACME"), any(Pageable.class))).thenReturn(page);

        var result = service.searchClients("ACME", 0, 10);
        assertEquals(1, result.getContent().size());
    }
}

`` 


## gateway-service\.idea\misc.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="MavenProjectsManager">
    <option name="originalFiles">
      <list>
        <option value="$PROJECT_DIR$/pom.xml" />
      </list>
    </option>
  </component>
  <component name="ProjectRootManager" version="2" languageLevel="JDK_25" default="true" project-jdk-name="25" project-jdk-type="JavaSDK">
    <output url="file://$PROJECT_DIR$/target" />
  </component>
</project>

`` 


## gateway-service\.idea\modules.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectModuleManager">
    <modules>
      <module fileurl="file://$PROJECT_DIR$/.idea/gateway-service.iml" filepath="$PROJECT_DIR$/.idea/gateway-service.iml" />
    </modules>
  </component>
</project>

`` 


## gateway-service\.idea\vcs.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="VcsDirectoryMappings">
    <mapping directory="$PROJECT_DIR$/../../.." vcs="Git" />
  </component>
</project>

`` 


## gateway-service\src\main\java\tg\ngstars\gateway\config\KeycloakJwtAuthenticationConverter.java

``java
package tg.ngstars.gateway.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakJwtAuthenticationConverter
        implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        var authorities = extractRoles(jwt);
        return Mono.just(new JwtAuthenticationToken(jwt, authorities));
    }

    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) return List.of();
        List<String> roles = (List<String>) realmAccess.getOrDefault("roles", List.of());
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .collect(Collectors.toList());
    }
}

`` 


## gateway-service\src\main\java\tg\ngstars\gateway\config\RateLimitConfig.java

``java
package tg.ngstars.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    @Bean
    @Primary
    public KeyResolver userKeyResolver() {
        return exchange -> exchange.getPrincipal()
            .map(principal -> principal.getName())
            .defaultIfEmpty("anonymous");
    }

    @Bean
    public KeyResolver remoteAddrKeyResolver() {
        return exchange -> Mono.just(
            exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown"
        );
    }
}

`` 


## gateway-service\src\main\java\tg\ngstars\gateway\config\SecurityConfig.java

``java
package tg.ngstars.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                .pathMatchers("/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").permitAll()
                .pathMatchers("/api/public/**").permitAll()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(
                    new KeycloakJwtAuthenticationConverter()
                ))
            )
            .build();
    }
}

`` 


## gateway-service\src\main\java\tg\ngstars\gateway\GatewayServiceApplication.java

``java
package tg.ngstars.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}

`` 


## gateway-service\src\main\resources\application-dev.yml

``yml
logging:
  level:
    tg.ngstars: DEBUG
    org.springframework.cloud.gateway: DEBUG

`` 


## gateway-service\src\main\resources\application-prod.yml

``yml
springdoc:
  swagger-ui:
    enabled: false
logging:
  level:
    tg.ngstars: WARN

`` 


## gateway-service\src\main\resources\application.yml

``yml
server:
  port: 8080
  shutdown: graceful

spring:
  application:
    name: gateway-service
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  lifecycle:
    timeout-per-shutdown-phase: 30s
  config:
    import: optional:file:${ENV_FILE:./.env}[.properties]

  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${KEYCLOAK_ISSUER_URI:http://localhost:8088/realms/ng-fields}

  cloud:
    gateway:
      httpclient:
        connect-timeout: 3000
        response-timeout: 5s

      globalcors:
        cors-configurations:
          '[/**]':
            allowed-origins:
              - "${FRONTEND_URL:http://localhost:4200}"
            allowed-methods: [GET, POST, PUT, PATCH, DELETE, OPTIONS]
            allowed-headers: ["*"]
            allow-credentials: true
            max-age: 3600

      routes:
        - id: auth-register
          uri: ${AUTH_SERVICE_URL:http://localhost:8081}
          predicates:
            - Path=/api/public/register
          filters:
            - StripPrefix=0
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 3
                redis-rate-limiter.burstCapacity: 6
                key-resolver: "#{@remoteAddrKeyResolver}"

        - id: auth-public
          uri: ${AUTH_SERVICE_URL:http://localhost:8081}
          predicates:
            - Path=/api/public/**
          filters:
            - StripPrefix=0

        - id: auth-admin
          uri: ${AUTH_SERVICE_URL:http://localhost:8081}
          predicates:
            - Path=/api/admin/users/**
          filters:
            - StripPrefix=0
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
                key-resolver: "#{@userKeyResolver}"

        - id: auth-me
          uri: ${AUTH_SERVICE_URL:http://localhost:8081}
          predicates:
            - Path=/api/users/me
          filters:
            - StripPrefix=0

        - id: client-service
          uri: ${CLIENT_SERVICE_URL:http://localhost:8082}
          predicates:
            - Path=/api/clients/**
          filters:
            - StripPrefix=0
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 20
                redis-rate-limiter.burstCapacity: 40
                key-resolver: "#{@userKeyResolver}"

        - id: intervention-service
          uri: ${INTERVENTION_SERVICE_URL:http://localhost:8083}
          predicates:
            - Path=/api/interventions/**
          filters:
            - StripPrefix=0
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 30
                redis-rate-limiter.burstCapacity: 60
                key-resolver: "#{@userKeyResolver}"

        - id: sync-service
          uri: ${INTERVENTION_SERVICE_URL:http://localhost:8083}
          predicates:
            - Path=/api/sync/**
          filters:
            - StripPrefix=0
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
                key-resolver: "#{@userKeyResolver}"

        - id: media-service
          uri: ${MEDIA_SERVICE_URL:http://localhost:8084}
          predicates:
            - Path=/api/media/**
          filters:
            - StripPrefix=0
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 20
                redis-rate-limiter.burstCapacity: 40
                key-resolver: "#{@userKeyResolver}"

        - id: notification-service
          uri: ${NOTIFICATION_SERVICE_URL:http://localhost:8085}
          predicates:
            - Path=/api/notifications/**
          filters:
            - StripPrefix=0
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
                key-resolver: "#{@userKeyResolver}"

        - id: report-service
          uri: ${REPORT_SERVICE_URL:http://localhost:8086}
          predicates:
            - Path=/api/reports/**
          filters:
            - StripPrefix=0
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 5
                redis-rate-limiter.burstCapacity: 10
                key-resolver: "#{@userKeyResolver}"

  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}

springdoc:
  swagger-ui:
    path: /swagger-ui.html
    urls:
      - name: auth-service
        url: /api/admin/users/v3/api-docs
      - name: client-service
        url: /api/clients/v3/api-docs
      - name: intervention-service
        url: /api/interventions/v3/api-docs
      - name: media-service
        url: /api/media/v3/api-docs
      - name: notification-service
        url: /api/notifications/v3/api-docs
      - name: report-service
        url: /api/reports/v3/api-docs

resilience4j:
  circuitbreaker:
    configs:
      default:
        sliding-window-size: 10
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10000ms
        permitted-number-of-calls-in-half-open-state: 3
  timelimiter:
    configs:
      default:
        timeout-duration: 5s

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized

`` 


## gateway-service\src\test\java\tg\ngstars\gateway\config\KeycloakJwtAuthenticationConverterTest.java

``java
package tg.ngstars.gateway.config;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

class KeycloakJwtAuthenticationConverterTest {

    final KeycloakJwtAuthenticationConverter converter = new KeycloakJwtAuthenticationConverter();

    @Test
    void shouldExtractRoleFromRealmAccess() {
        var jwt = Jwt.withTokenValue("t")
                .header("alg", "RS256")
                .subject("user-123")
                .claim("realm_access", Map.of("roles", List.of("ADMIN", "MANAGER")))
                .build();

        var auth = converter.convert(jwt).block();

        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER")));
    }

    @Test
    void shouldReturnEmptyAuthoritiesWhenNoRealmAccess() {
        var jwt = Jwt.withTokenValue("t")
                .header("alg", "RS256")
                .subject("user-123")
                .build();

        var auth = converter.convert(jwt).block();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().isEmpty());
    }

    @Test
    void shouldReturnEmptyAuthoritiesWhenNoRoles() {
        var jwt = Jwt.withTokenValue("t")
                .header("alg", "RS256")
                .subject("user-123")
                .claim("realm_access", Map.of())
                .build();

        var auth = converter.convert(jwt).block();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().isEmpty());
    }

    @Test
    void shouldKeepSubjectInToken() {
        var jwt = Jwt.withTokenValue("t")
                .header("alg", "RS256")
                .subject("user-456")
                .claim("realm_access", Map.of("roles", List.of("TECHNICIAN")))
                .build();

        var auth = converter.convert(jwt).block();
        assertNotNull(auth);
        assertEquals("user-456", auth.getName());
    }
}

`` 


## intervention-service\.idea\misc.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="MavenProjectsManager">
    <option name="originalFiles">
      <list>
        <option value="$PROJECT_DIR$/pom.xml" />
      </list>
    </option>
  </component>
  <component name="ProjectRootManager" version="2" languageLevel="JDK_25" default="true" project-jdk-name="25" project-jdk-type="JavaSDK">
    <output url="file://$PROJECT_DIR$/target" />
  </component>
</project>

`` 


## intervention-service\.idea\modules.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectModuleManager">
    <modules>
      <module fileurl="file://$PROJECT_DIR$/.idea/intervention-service.iml" filepath="$PROJECT_DIR$/.idea/intervention-service.iml" />
    </modules>
  </component>
</project>

`` 


## intervention-service\.idea\vcs.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="VcsDirectoryMappings">
    <mapping directory="$PROJECT_DIR$/../../.." vcs="Git" />
  </component>
</project>

`` 


## intervention-service\src\main\java\tg\ngstars\interv\client\MediaClient.java

``java
package tg.ngstars.interv.client;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Component
public class MediaClient {

    private final RestClient restClient;
    private final String mediaBaseUrl;

    public MediaClient(
            @Value("${media-service.url:http://localhost:8084}") String mediaBaseUrl) {
        this.mediaBaseUrl = mediaBaseUrl;
        var httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        var factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(10));
        this.restClient = RestClient.builder()
                .baseUrl(mediaBaseUrl + "/api/media")
                .requestFactory(factory)
                .requestInterceptor((request, body, execution) -> {
                    var auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                        request.getHeaders().setBearerAuth(jwt.getTokenValue());
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

    @SuppressWarnings("unchecked")
    public String uploadFile(MultipartFile file) {
        var body = restClient.post()
                .uri("/upload")
                .body(createMultipartBody(file))
                .retrieve()
                .body(Map.class);
        var filename = (String) body.get("filename");
        return mediaBaseUrl + "/api/media/" + filename;
    }

    @SuppressWarnings("unchecked")
    public String uploadBase64(String base64Data) {
        String data = base64Data.replaceAll("^data:image/[^;]+;base64,", "");
        var body = restClient.post()
                .uri("/upload-base64")
                .body(Map.of("data", data))
                .retrieve()
                .body(Map.class);
        var filename = (String) body.get("filename");
        return mediaBaseUrl + "/api/media/" + filename;
    }

    public void deleteFile(String filename) {
        restClient.delete()
                .uri("/{filename}", filename)
                .retrieve();
    }

    private org.springframework.http.HttpEntity<?> createMultipartBody(MultipartFile file) {
        var headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);
        var body = new org.springframework.util.LinkedMultiValueMap<String, Object>();
        body.add("file", file.getResource());
        return new org.springframework.http.HttpEntity<>(body, headers);
    }
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\config\GlobalExceptionHandler.java

``java
package tg.ngstars.interv.config;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tg.ngstars.interv.exception.ForbiddenException;
import tg.ngstars.interv.exception.NotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Bad Request");
        problem.setDetail(ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed"));
        problem.setType(URI.create("about:blank"));
        return problem;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problem.setTitle("Forbidden");
        problem.setDetail("Access denied");
        problem.setType(URI.create("about:blank"));
        return problem;
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Not Found");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("about:blank"));
        return problem;
    }

    @ExceptionHandler(ForbiddenException.class)
    public ProblemDetail handleForbidden(ForbiddenException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problem.setTitle("Forbidden");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("about:blank"));
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Bad Request");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("about:blank"));
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        var problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Internal Server Error");
        problem.setDetail("An unexpected error occurred");
        problem.setType(URI.create("about:blank"));
        return problem;
    }
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\config\SecurityConfig.java

``java
package tg.ngstars.interv.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origins:http://localhost:4200,http://localhost:8100}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new RealmRoleConverter());
        return converter;
    }

    static class RealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        @SuppressWarnings("unchecked")
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null) return List.of();
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            if (roles == null) return List.of();
            return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        }
    }
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\controller\InterventionController.java

``java
package tg.ngstars.interv.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tg.ngstars.interv.dto.CreateInterventionRequest;
import tg.ngstars.interv.dto.InterventionResponse;
import tg.ngstars.interv.dto.ItemRequest;
import tg.ngstars.interv.dto.UpdateBillingRequest;
import tg.ngstars.interv.dto.UpdateDiagnosisRequest;
import tg.ngstars.interv.dto.UpdateEquipmentRequest;
import tg.ngstars.interv.dto.UpdateRecommendationsRequest;
import tg.ngstars.interv.dto.UpdateResultRequest;
import tg.ngstars.interv.dto.UpdateScheduleRequest;
import tg.ngstars.interv.service.InterventionService;
import tg.ngstars.interv.service.SecurityUtils;

@RestController
@RequestMapping("/api/interventions")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
public class InterventionController {

    private final InterventionService interventionService;
    private final SecurityUtils securityUtils;

    public InterventionController(InterventionService interventionService, SecurityUtils securityUtils) {
        this.interventionService = interventionService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<InterventionResponse> createIntervention(
            @Valid @RequestBody CreateInterventionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(interventionService.createIntervention(request, securityUtils.getCurrentUserId()));
    }

    @GetMapping
    public ResponseEntity<Page<InterventionResponse>> getInterventions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID technicianId,
            Pageable pageable) {
        var currentUserId = securityUtils.getCurrentUserId();
        var isAdminOrManager = securityUtils.isAdminOrManager();
        var techId = isAdminOrManager ? technicianId : currentUserId;
        return ResponseEntity.ok(interventionService.getInterventions(status, techId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterventionResponse> getIntervention(@PathVariable UUID id) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.getIntervention(id, userId, securityUtils.isAdminOrManager()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InterventionResponse> updateIntervention(@PathVariable UUID id,
            @Valid @RequestBody CreateInterventionRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateIntervention(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIntervention(@PathVariable UUID id) {
        var userId = securityUtils.getCurrentUserId();
        interventionService.deleteIntervention(id, userId, securityUtils.isAdminOrManager());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generatePdf(@PathVariable UUID id) {
        var userId = securityUtils.getCurrentUserId();
        var pdf = interventionService.generatePdf(id, userId, securityUtils.isAdminOrManager());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=intervention.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/by-client/{clientId}")
    public ResponseEntity<List<InterventionResponse>> getClientInterventions(
            @PathVariable UUID clientId) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.getClientInterventions(clientId, userId, securityUtils.isAdminOrManager()));
    }

    @PatchMapping("/{id}/schedule")
    public ResponseEntity<InterventionResponse> updateSchedule(@PathVariable UUID id,
            @Valid @RequestBody UpdateScheduleRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateSchedule(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PatchMapping("/{id}/equipment")
    public ResponseEntity<InterventionResponse> updateEquipment(@PathVariable UUID id,
            @Valid @RequestBody UpdateEquipmentRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateEquipment(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PatchMapping("/{id}/diagnosis")
    public ResponseEntity<InterventionResponse> updateDiagnosis(@PathVariable UUID id,
            @Valid @RequestBody UpdateDiagnosisRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateDiagnosis(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PatchMapping("/{id}/result")
    public ResponseEntity<InterventionResponse> updateResult(@PathVariable UUID id,
            @Valid @RequestBody UpdateResultRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateResult(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PatchMapping("/{id}/recommendations")
    public ResponseEntity<InterventionResponse> updateRecommendations(@PathVariable UUID id,
            @Valid @RequestBody UpdateRecommendationsRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateRecommendations(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PatchMapping("/{id}/billing")
    public ResponseEntity<InterventionResponse> updateBilling(@PathVariable UUID id,
            @Valid @RequestBody UpdateBillingRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateBilling(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<InterventionResponse> addItem(@PathVariable UUID id,
            @Valid @RequestBody ItemRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(interventionService.addItem(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PutMapping("/{id}/items/{itemId}")
    public ResponseEntity<InterventionResponse> updateItem(@PathVariable UUID id, @PathVariable UUID itemId,
            @Valid @RequestBody ItemRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateItem(id, itemId, request, userId, securityUtils.isAdminOrManager()));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<InterventionResponse> removeItem(@PathVariable UUID id, @PathVariable UUID itemId) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.removeItem(id, itemId, userId, securityUtils.isAdminOrManager()));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<InterventionResponse> closeIntervention(@PathVariable UUID id) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.closeIntervention(id, userId, securityUtils.isAdminOrManager()));
    }
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\controller\PhotoController.java

``java
package tg.ngstars.interv.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tg.ngstars.interv.dto.PhotoResponse;
import tg.ngstars.interv.service.PhotoService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/interventions/{id}/photos")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TECHNICIAN','MANAGER','ADMIN')")
    public ResponseEntity<PhotoResponse> upload(
            @PathVariable UUID id,
            @RequestParam("file")      MultipartFile file,
            @RequestParam("type")      String type,
            @RequestParam(value = "latitude",  required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude
    ) throws IOException {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(photoService.addPhoto(id, file, type, latitude, longitude));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PhotoResponse>> list(@PathVariable UUID id) {
        return ResponseEntity.ok(photoService.listPhotos(id));
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PhotoResponse>> listByType(
            @PathVariable UUID id,
            @PathVariable String type) {
        return ResponseEntity.ok(photoService.listPhotosByType(id, type));
    }

    @DeleteMapping("/{photoId}")
    @PreAuthorize("hasAnyRole('TECHNICIAN','MANAGER','ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @PathVariable UUID photoId) {
        photoService.deletePhoto(id, photoId);
        return ResponseEntity.noContent().build();
    }
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\controller\SignatureController.java

``java
package tg.ngstars.interv.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tg.ngstars.interv.dto.SignatureRequest;
import tg.ngstars.interv.service.SignatureService;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/interventions/{id}/signatures")
public class SignatureController {

    private final SignatureService signatureService;

    public SignatureController(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    @PostMapping("/client")
    @PreAuthorize("hasAnyRole('TECHNICIAN','MANAGER','ADMIN')")
    public ResponseEntity<Map<String, String>> signClient(
            @PathVariable UUID id,
            @Valid @RequestBody SignatureRequest req) throws IOException {
        String url = signatureService.signClient(id, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "message", "Signature client enregistrée",
            "url", url
        ));
    }

    @PostMapping("/technician")
    @PreAuthorize("hasAnyRole('TECHNICIAN','MANAGER','ADMIN')")
    public ResponseEntity<Map<String, String>> signTechnician(
            @PathVariable UUID id,
            @Valid @RequestBody SignatureRequest req) throws IOException {
        String url = signatureService.signTechnician(id, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "message", "Signature technicien enregistrée",
            "url", url
        ));
    }

    @PostMapping("/manager")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<Map<String, String>> signManager(
            @PathVariable UUID id,
            @Valid @RequestBody SignatureRequest req) throws IOException {
        String url = signatureService.signManager(id, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "message", "Signature manager enregistrée. Intervention validée.",
            "url", url
        ));
    }
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\controller\SyncController.java

``java
package tg.ngstars.interv.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tg.ngstars.interv.dto.InterventionResponse;
import tg.ngstars.interv.dto.SyncRequest;
import tg.ngstars.interv.service.InterventionService;
import tg.ngstars.interv.service.SecurityUtils;

@RestController
@RequestMapping("/api/sync")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
public class SyncController {

    private final InterventionService interventionService;
    private final SecurityUtils securityUtils;

    public SyncController(InterventionService interventionService, SecurityUtils securityUtils) {
        this.interventionService = interventionService;
        this.securityUtils = securityUtils;
    }

    @PostMapping("/interventions")
    public ResponseEntity<InterventionResponse> syncIntervention(@Valid @RequestBody SyncRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.syncFromMobile(request, userId, securityUtils.isAdminOrManager()));
    }
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\dto\CreateInterventionRequest.java

``java
package tg.ngstars.interv.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateInterventionRequest(
    @NotBlank String reference,
    @NotNull UUID clientId,
    String clientName,
    String clientEmail,
    String clientPhone,
    String clientAddress,
    String equipmentType,
    String equipmentBrand,
    String equipmentModel,
    String equipmentSerial,
    String equipmentLocation,
    String reportedIssue,
    String openprojectTicketId,
    String openprojectTicketUrl,
    String diagnosis,
    String workDone,
    String status,
    OffsetDateTime interventionDate,
    UUID assignedTo,
    String siteAddress,
    String siteCity,
    BigDecimal estimatedCost,
    String notes,
    List<CreateItemRequest> items
) {

    public record CreateItemRequest(
        @NotBlank String type,
        @NotBlank String description,
        Integer quantity,
        BigDecimal unitPrice
    ) {}
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\dto\InterventionResponse.java

``java
package tg.ngstars.interv.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record InterventionResponse(
    UUID id,
    String reference,
    UUID clientId,
    String clientName,
    String clientEmail,
    String clientPhone,
    String clientAddress,
    String equipmentType,
    String equipmentBrand,
    String equipmentModel,
    String equipmentSerial,
    String equipmentLocation,
    String reportedIssue,
    String openprojectTicketId,
    String openprojectTicketUrl,
    String diagnosis,
    String workDone,
    String status,
    OffsetDateTime interventionDate,
    UUID createdBy,
    UUID assignedTo,
    String siteAddress,
    String siteCity,
    BigDecimal estimatedCost,
    BigDecimal totalCost,
    String clientSignature,
    String technicianSignature,
    String managerSignature,
    OffsetDateTime signedAt,
    OffsetDateTime departureTime,
    OffsetDateTime arrivalTime,
    OffsetDateTime startTime,
    OffsetDateTime endTime,
    Integer durationMinutes,
    String result,
    String recommendations,
    Boolean billable,
    BigDecimal billingAmount,
    String billingNotes,
    String localId,
    String notes,
    boolean active,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    List<ItemResponse> items
) {

    public record ItemResponse(
        UUID id,
        String type,
        String description,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal total,
        OffsetDateTime createdAt
    ) {}
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\dto\ItemRequest.java

``java
package tg.ngstars.interv.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ItemRequest(
    @NotBlank String type,
    @NotBlank String description,
    @Min(1) Integer quantity,
    BigDecimal unitPrice
) {}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\dto\ItemResponse.java

``java
package tg.ngstars.interv.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ItemResponse(
    UUID id,
    String type,
    String description,
    int quantity,
    BigDecimal unitPrice,
    BigDecimal total,
    OffsetDateTime createdAt
) {}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\dto\PhotoResponse.java

``java
package tg.ngstars.interv.dto;

import tg.ngstars.interv.model.InterventionPhoto;
import tg.ngstars.interv.model.PhotoType;

import java.time.Instant;
import java.util.UUID;

public record PhotoResponse(
    UUID      id,
    String    url,
    PhotoType type,
    Double    latitude,
    Double    longitude,
    Instant   takenAt,
    String    originalFilename,
    Instant   createdAt
) {
    public static PhotoResponse from(InterventionPhoto p) {
        return new PhotoResponse(
            p.getId(), p.getUrl(), p.getType(),
            p.getLatitude(), p.getLongitude(),
            p.getTakenAt(), p.getOriginalFilename(),
            p.getCreatedAt()
        );
    }
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\dto\SignatureRequest.java

``java
package tg.ngstars.interv.dto;

import jakarta.validation.constraints.NotBlank;

public record SignatureRequest(
    @NotBlank String imageBase64,
    String signatoryName
) {}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\dto\SyncRequest.java

``java
package tg.ngstars.interv.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SyncRequest(
    @NotBlank String reference,
    @NotNull UUID clientId,
    String clientName,
    String clientEmail,
    String clientPhone,
    String clientAddress,
    String equipmentType,
    String equipmentBrand,
    String equipmentModel,
    String equipmentSerial,
    String reportedIssue,
    String status,
    OffsetDateTime interventionDate,
    String siteAddress,
    String siteCity,
    @NotBlank String localId
) {}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\dto\UpdateBillingRequest.java

``java
package tg.ngstars.interv.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateBillingRequest(
    boolean billable,
    @Positive BigDecimal billingAmount,
    @Size(max = 1000) String billingNotes
) {}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\dto\UpdateDiagnosisRequest.java

``java
package tg.ngstars.interv.dto;

import jakarta.validation.constraints.Size;

public record UpdateDiagnosisRequest(
    @Size(max = 5000) String diagnosis,
    @Size(max = 5000) String workDone
) {}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\dto\UpdateEquipmentRequest.java

``java
package tg.ngstars.interv.dto;

import jakarta.validation.constraints.Size;

public record UpdateEquipmentRequest(
    @Size(max = 100) String brand,
    @Size(max = 100) String model,
    @Size(max = 100) String serial,
    @Size(max = 200) String location,
    @Size(max = 2000) String problemDescription,
    @Size(max = 50) String openprojectTicketId,
    @Size(max = 500) String openprojectTicketUrl
) {}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\dto\UpdateRecommendationsRequest.java

``java
package tg.ngstars.interv.dto;

import jakarta.validation.constraints.Size;

public record UpdateRecommendationsRequest(
    @Size(max = 5000) String recommendations
) {}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\dto\UpdateResultRequest.java

``java
package tg.ngstars.interv.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateResultRequest(
    @NotBlank String result
) {}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\dto\UpdateScheduleRequest.java

``java
package tg.ngstars.interv.dto;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.FutureOrPresent;

public record UpdateScheduleRequest(
    @FutureOrPresent OffsetDateTime departureTime,
    @FutureOrPresent OffsetDateTime arrivalTime,
    @FutureOrPresent OffsetDateTime startTime,
    @FutureOrPresent OffsetDateTime endTime
) {}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\exception\ForbiddenException.java

``java
package tg.ngstars.interv.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) { super(message); }
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\exception\NotFoundException.java

``java
package tg.ngstars.interv.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\model\Intervention.java

``java
package tg.ngstars.interv.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "interventions")
@Getter @Setter @ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Intervention {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "client_email")
    private String clientEmail;

    @Column(name = "client_phone")
    private String clientPhone;

    @Column(name = "client_address")
    private String clientAddress;

    @Column(name = "equipment_type")
    private String equipmentType;

    @Column(name = "equipment_brand")
    private String equipmentBrand;

    @Column(name = "equipment_model")
    private String equipmentModel;

    @Column(name = "equipment_serial")
    private String equipmentSerial;

    @Column(name = "equipment_location")
    private String equipmentLocation;

    @Column(name = "reported_issue")
    private String reportedIssue;

    @Column(name = "openproject_ticket_id")
    private String openprojectTicketId;

    @Column(name = "openproject_ticket_url")
    private String openprojectTicketUrl;

    private String diagnosis;

    @Column(name = "work_done")
    private String workDone;

    @Column(nullable = false)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "intervention_date")
    private OffsetDateTime interventionDate;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "assigned_to")
    private UUID assignedTo;

    @Column(name = "site_address")
    private String siteAddress;

    @Column(name = "site_city")
    private String siteCity;

    @Column(name = "estimated_cost")
    private BigDecimal estimatedCost;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @Column(name = "client_signature")
    private String clientSignature;

    @Column(name = "technician_signature")
    private String technicianSignature;

    @Column(name = "manager_signature")
    private String managerSignature;

    @Column(name = "signed_at")
    private OffsetDateTime signedAt;

    @Column(name = "departure_time")
    private OffsetDateTime departureTime;

    @Column(name = "arrival_time")
    private OffsetDateTime arrivalTime;

    @Column(name = "start_time")
    private OffsetDateTime startTime;

    @Column(name = "end_time")
    private OffsetDateTime endTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(length = 20)
    private String result;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @Column(nullable = false)
    @Builder.Default
    private Boolean billable = true;

    @Column(name = "billing_amount")
    private BigDecimal billingAmount;

    @Column(name = "billing_notes")
    private String billingNotes;

    @Column(name = "local_id", unique = true)
    private String localId;

    private String notes;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Version
    private Integer version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "intervention", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<InterventionItem> items = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        var now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\model\InterventionItem.java

``java
package tg.ngstars.interv.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "intervention_items")
@Getter @Setter @ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterventionItem {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intervention_id", nullable = false)
    private Intervention intervention;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Column(name = "unit_price", nullable = false)
    @Builder.Default
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Version
    private Integer version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = OffsetDateTime.now();
        total = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\model\InterventionPhoto.java

``java
package tg.ngstars.interv.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "intervention_photos")
public class InterventionPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intervention_id", nullable = false)
    private Intervention intervention;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PhotoType type;

    private Double latitude;
    private Double longitude;

    @Column(name = "taken_at")
    private Instant takenAt;

    @Column(name = "original_filename", length = 200)
    private String originalFilename;

    @Version
    private Integer version;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() { createdAt = Instant.now(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Intervention getIntervention() { return intervention; }
    public void setIntervention(Intervention i) { this.intervention = i; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public PhotoType getType() { return type; }
    public void setType(PhotoType type) { this.type = type; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double lat) { this.latitude = lat; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double lon) { this.longitude = lon; }
    public Instant getTakenAt() { return takenAt; }
    public void setTakenAt(Instant t) { this.takenAt = t; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String f) { this.originalFilename = f; }
    public Instant getCreatedAt() { return createdAt; }
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\model\PhotoType.java

``java
package tg.ngstars.interv.model;

public enum PhotoType {
    BEFORE,
    AFTER
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\repository\InterventionPhotoRepository.java

``java
package tg.ngstars.interv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tg.ngstars.interv.model.InterventionPhoto;
import tg.ngstars.interv.model.PhotoType;

import java.util.List;
import java.util.UUID;

@Repository
public interface InterventionPhotoRepository extends JpaRepository<InterventionPhoto, UUID> {

    List<InterventionPhoto> findByInterventionId(UUID interventionId);

    List<InterventionPhoto> findByInterventionIdAndType(UUID interventionId, PhotoType type);

    long countByInterventionIdAndType(UUID interventionId, PhotoType type);
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\repository\InterventionRepository.java

``java
package tg.ngstars.interv.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import tg.ngstars.interv.model.Intervention;

public interface InterventionRepository extends JpaRepository<Intervention, UUID> {

    Optional<Intervention> findByReference(String reference);

    @EntityGraph(attributePaths = {"items"})
    List<Intervention> findByClientIdOrderByCreatedAtDesc(UUID clientId);

    @EntityGraph(attributePaths = {"items"})
    Page<Intervention> findByActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    boolean existsByReference(String reference);

    Optional<Intervention> findByLocalId(String localId);

    @EntityGraph(attributePaths = {"items"})
    Page<Intervention> findByActiveTrueAndStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    @EntityGraph(attributePaths = {"items"})
    Page<Intervention> findByActiveTrueAndAssignedToOrderByCreatedAtDesc(UUID assignedTo, Pageable pageable);

    @EntityGraph(attributePaths = {"items"})
    Page<Intervention> findByActiveTrueAndAssignedToAndStatusOrderByCreatedAtDesc(UUID assignedTo, String status, Pageable pageable);
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\service\InterventionService.java

``java
package tg.ngstars.interv.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tg.ngstars.interv.dto.CreateInterventionRequest;
import tg.ngstars.interv.dto.CreateInterventionRequest.CreateItemRequest;
import tg.ngstars.interv.dto.InterventionResponse;
import tg.ngstars.interv.dto.InterventionResponse.ItemResponse;
import tg.ngstars.interv.dto.ItemRequest;
import tg.ngstars.interv.dto.SyncRequest;
import tg.ngstars.interv.dto.UpdateBillingRequest;
import tg.ngstars.interv.dto.UpdateDiagnosisRequest;
import tg.ngstars.interv.dto.UpdateEquipmentRequest;
import tg.ngstars.interv.dto.UpdateRecommendationsRequest;
import tg.ngstars.interv.dto.UpdateResultRequest;
import tg.ngstars.interv.dto.UpdateScheduleRequest;
import tg.ngstars.interv.exception.ForbiddenException;
import tg.ngstars.interv.exception.NotFoundException;
import tg.ngstars.interv.model.Intervention;
import tg.ngstars.interv.model.InterventionItem;
import tg.ngstars.interv.repository.InterventionRepository;

@Service
@Transactional(readOnly = true)
public class InterventionService {

    private final InterventionRepository interventionRepository;

    public InterventionService(InterventionRepository interventionRepository) {
        this.interventionRepository = interventionRepository;
    }

    private Intervention findOrThrow(UUID id) {
        return interventionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Intervention not found: " + id));
    }

    private void checkOwnership(Intervention intervention, UUID userId, boolean isAdminOrManager) {
        if (isAdminOrManager) return;
        if (intervention.getAssignedTo() == null || !intervention.getAssignedTo().equals(userId))
            throw new ForbiddenException("Not assigned to this intervention");
    }

    @Transactional
    public InterventionResponse createIntervention(CreateInterventionRequest request, UUID userId) {
        if (interventionRepository.existsByReference(request.reference()))
            throw new IllegalArgumentException("Reference already exists: " + request.reference());

        var intervention = Intervention.builder()
                .reference(request.reference())
                .clientId(request.clientId())
                .clientName(request.clientName())
                .clientEmail(request.clientEmail())
                .clientPhone(request.clientPhone())
                .clientAddress(request.clientAddress())
                .equipmentType(request.equipmentType())
                .equipmentBrand(request.equipmentBrand())
                .equipmentModel(request.equipmentModel())
                .equipmentSerial(request.equipmentSerial())
                .equipmentLocation(request.equipmentLocation())
                .reportedIssue(request.reportedIssue())
                .openprojectTicketId(request.openprojectTicketId())
                .openprojectTicketUrl(request.openprojectTicketUrl())
                .diagnosis(request.diagnosis())
                .workDone(request.workDone())
                .status(request.status() != null ? request.status() : "PENDING")
                .interventionDate(request.interventionDate())
                .createdBy(userId)
                .assignedTo(request.assignedTo())
                .siteAddress(request.siteAddress())
                .siteCity(request.siteCity())
                .estimatedCost(request.estimatedCost())
                .notes(request.notes())
                .active(true)
                .build();

        if (request.items() != null) {
            var items = request.items().stream().map(itemReq -> {
                var unitPrice = itemReq.unitPrice() != null ? itemReq.unitPrice() : BigDecimal.ZERO;
                var quantity = itemReq.quantity() != null ? itemReq.quantity() : 1;
                return InterventionItem.builder()
                        .intervention(intervention)
                        .type(itemReq.type())
                        .description(itemReq.description())
                        .quantity(quantity)
                        .unitPrice(unitPrice)
                        .total(unitPrice.multiply(BigDecimal.valueOf(quantity)))
                        .build();
            }).toList();
            intervention.setItems(items);
            intervention.setTotalCost(intervention.getItems().stream()
                    .map(InterventionItem::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        return toResponse(interventionRepository.save(intervention));
    }

    public Page<InterventionResponse> getInterventions(String status, UUID technicianId, Pageable pageable) {
        if (technicianId != null)
            return (status != null
                    ? interventionRepository.findByActiveTrueAndAssignedToAndStatusOrderByCreatedAtDesc(technicianId, status, pageable)
                    : interventionRepository.findByActiveTrueAndAssignedToOrderByCreatedAtDesc(technicianId, pageable))
                    .map(this::toResponse);
        if (status != null)
            return interventionRepository.findByActiveTrueAndStatusOrderByCreatedAtDesc(status, pageable)
                    .map(this::toResponse);
        return interventionRepository.findByActiveTrueOrderByCreatedAtDesc(pageable)
                .map(this::toResponse);
    }

    public InterventionResponse getIntervention(UUID id, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        return toResponse(intervention);
    }

    @Transactional
    public InterventionResponse updateIntervention(UUID id, CreateInterventionRequest request, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);

        intervention.setReference(request.reference());
        intervention.setClientId(request.clientId());
        intervention.setClientName(request.clientName());
        intervention.setClientEmail(request.clientEmail());
        intervention.setClientPhone(request.clientPhone());
        intervention.setClientAddress(request.clientAddress());
        intervention.setEquipmentType(request.equipmentType());
        intervention.setEquipmentBrand(request.equipmentBrand());
        intervention.setEquipmentModel(request.equipmentModel());
        intervention.setEquipmentSerial(request.equipmentSerial());
        intervention.setEquipmentLocation(request.equipmentLocation());
        intervention.setReportedIssue(request.reportedIssue());
        intervention.setOpenprojectTicketId(request.openprojectTicketId());
        intervention.setOpenprojectTicketUrl(request.openprojectTicketUrl());
        intervention.setDiagnosis(request.diagnosis());
        intervention.setWorkDone(request.workDone());
        if (request.status() != null) intervention.setStatus(request.status());
        intervention.setInterventionDate(request.interventionDate());
        intervention.setAssignedTo(request.assignedTo());
        intervention.setSiteAddress(request.siteAddress());
        intervention.setSiteCity(request.siteCity());
        intervention.setEstimatedCost(request.estimatedCost());
        intervention.setNotes(request.notes());

        if (request.items() != null) {
            intervention.getItems().clear();
            var items = request.items().stream().map(itemReq -> {
                var unitPrice = itemReq.unitPrice() != null ? itemReq.unitPrice() : BigDecimal.ZERO;
                var quantity = itemReq.quantity() != null ? itemReq.quantity() : 1;
                return InterventionItem.builder()
                        .intervention(intervention)
                        .type(itemReq.type())
                        .description(itemReq.description())
                        .quantity(quantity)
                        .unitPrice(unitPrice)
                        .total(unitPrice.multiply(BigDecimal.valueOf(quantity)))
                        .build();
            }).toList();
            intervention.getItems().addAll(items);
            intervention.setTotalCost(items.stream()
                    .map(InterventionItem::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public void deleteIntervention(UUID id, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        intervention.setActive(false);
        interventionRepository.save(intervention);
    }

    public List<InterventionResponse> getClientInterventions(UUID clientId, UUID userId, boolean isAdminOrManager) {
        return interventionRepository.findByClientIdOrderByCreatedAtDesc(clientId).stream()
                .filter(i -> isAdminOrManager || (i.getAssignedTo() != null && i.getAssignedTo().equals(userId)))
                .map(this::toResponse).toList();
    }

    public byte[] generatePdf(UUID id, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        return PdfService.generate(intervention);
    }

    @Transactional
    public InterventionResponse updateSchedule(UUID id, UpdateScheduleRequest request, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        if (request.departureTime() != null) intervention.setDepartureTime(request.departureTime());
        if (request.arrivalTime() != null) intervention.setArrivalTime(request.arrivalTime());
        if (request.startTime() != null) intervention.setStartTime(request.startTime());
        if (request.endTime() != null) {
            intervention.setEndTime(request.endTime());
            if (intervention.getStartTime() != null)
                intervention.setDurationMinutes((int) java.time.Duration.between(intervention.getStartTime(), request.endTime()).toMinutes());
        }
        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public InterventionResponse updateEquipment(UUID id, UpdateEquipmentRequest request, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        if (request.brand() != null) intervention.setEquipmentBrand(request.brand());
        if (request.model() != null) intervention.setEquipmentModel(request.model());
        if (request.serial() != null) intervention.setEquipmentSerial(request.serial());
        if (request.location() != null) intervention.setEquipmentLocation(request.location());
        if (request.problemDescription() != null) intervention.setReportedIssue(request.problemDescription());
        if (request.openprojectTicketId() != null) intervention.setOpenprojectTicketId(request.openprojectTicketId());
        if (request.openprojectTicketUrl() != null) intervention.setOpenprojectTicketUrl(request.openprojectTicketUrl());
        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public InterventionResponse updateDiagnosis(UUID id, UpdateDiagnosisRequest request, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        if (request.diagnosis() != null) intervention.setDiagnosis(request.diagnosis());
        if (request.workDone() != null) intervention.setWorkDone(request.workDone());
        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public InterventionResponse updateResult(UUID id, UpdateResultRequest request, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        intervention.setResult(request.result());
        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public InterventionResponse updateRecommendations(UUID id, UpdateRecommendationsRequest request, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        intervention.setRecommendations(request.recommendations());
        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public InterventionResponse updateBilling(UUID id, UpdateBillingRequest request, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        intervention.setBillable(request.billable());
        intervention.setBillingAmount(request.billingAmount());
        intervention.setBillingNotes(request.billingNotes());
        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public InterventionResponse addItem(UUID id, ItemRequest request, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        var unitPrice = request.unitPrice() != null ? request.unitPrice() : BigDecimal.ZERO;
        var quantity = request.quantity() != null ? request.quantity() : 1;
        var item = InterventionItem.builder()
                .intervention(intervention)
                .type(request.type())
                .description(request.description())
                .quantity(quantity)
                .unitPrice(unitPrice)
                .total(unitPrice.multiply(BigDecimal.valueOf(quantity)))
                .build();
        intervention.getItems().add(item);
        intervention.setTotalCost(intervention.getItems().stream()
                .map(InterventionItem::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public InterventionResponse updateItem(UUID interventionId, UUID itemId, ItemRequest request, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(interventionId);
        checkOwnership(intervention, userId, isAdminOrManager);
        var item = intervention.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));
        if (request.type() != null) item.setType(request.type());
        if (request.description() != null) item.setDescription(request.description());
        if (request.quantity() != null) item.setQuantity(request.quantity());
        if (request.unitPrice() != null) item.setUnitPrice(request.unitPrice());
        item.setTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        intervention.setTotalCost(intervention.getItems().stream()
                .map(InterventionItem::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public InterventionResponse removeItem(UUID interventionId, UUID itemId, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(interventionId);
        checkOwnership(intervention, userId, isAdminOrManager);
        var removed = intervention.getItems().removeIf(i -> i.getId().equals(itemId));
        if (!removed) throw new NotFoundException("Item not found: " + itemId);
        intervention.setTotalCost(intervention.getItems().stream()
                .map(InterventionItem::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public InterventionResponse closeIntervention(UUID id, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        if ("COMPLETED".equals(intervention.getStatus()))
            throw new IllegalStateException("Intervention already completed");
        checkOwnership(intervention, userId, isAdminOrManager);
        if (intervention.getClientSignature() != null
                && intervention.getTechnicianSignature() != null
                && intervention.getManagerSignature() != null) {
            intervention.setStatus("COMPLETED");
            intervention.setSignedAt(java.time.OffsetDateTime.now());
        }
        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public InterventionResponse syncFromMobile(SyncRequest request, UUID userId, boolean isAdminOrManager) {
        var existing = interventionRepository.findByLocalId(request.localId());
        if (existing.isPresent()) {
            var intervention = existing.get();
            checkOwnership(intervention, userId, isAdminOrManager);
            intervention.setStatus(request.status() != null ? request.status() : intervention.getStatus());
            intervention.setInterventionDate(request.interventionDate() != null ? request.interventionDate() : intervention.getInterventionDate());
            if (request.clientName() != null) intervention.setClientName(request.clientName());
            intervention.setClientEmail(request.clientEmail() != null ? request.clientEmail() : intervention.getClientEmail());
            intervention.setClientPhone(request.clientPhone() != null ? request.clientPhone() : intervention.getClientPhone());
            intervention.setClientAddress(request.clientAddress() != null ? request.clientAddress() : intervention.getClientAddress());
            return toResponse(interventionRepository.save(intervention));
        }
        var intervention = Intervention.builder()
                .reference(request.localId())
                .clientId(request.clientId())
                .clientName(request.clientName())
                .clientEmail(request.clientEmail())
                .clientPhone(request.clientPhone())
                .clientAddress(request.clientAddress())
                .equipmentType(request.equipmentType())
                .equipmentBrand(request.equipmentBrand())
                .equipmentModel(request.equipmentModel())
                .equipmentSerial(request.equipmentSerial())
                .reportedIssue(request.reportedIssue())
                .status(request.status() != null ? request.status() : "PENDING")
                .interventionDate(request.interventionDate())
                .createdBy(userId)
                .assignedTo(userId)
                .siteAddress(request.siteAddress())
                .siteCity(request.siteCity())
                .localId(request.localId())
                .active(true)
                .build();
        return toResponse(interventionRepository.save(intervention));
    }

    private InterventionResponse toResponse(Intervention i) {
        var items = i.getItems() != null
                ? i.getItems().stream().<ItemResponse>map(item -> new ItemResponse(
                        item.getId(), item.getType(), item.getDescription(),
                        item.getQuantity(), item.getUnitPrice(), item.getTotal(),
                        item.getCreatedAt())).toList()
                : List.<ItemResponse>of();

        return new InterventionResponse(
                i.getId(), i.getReference(), i.getClientId(),
                i.getClientName(), i.getClientEmail(), i.getClientPhone(),
                i.getClientAddress(), i.getEquipmentType(), i.getEquipmentBrand(),
                i.getEquipmentModel(), i.getEquipmentSerial(),
                i.getEquipmentLocation(), i.getReportedIssue(),
                i.getOpenprojectTicketId(), i.getOpenprojectTicketUrl(),
                i.getDiagnosis(), i.getWorkDone(), i.getStatus(), i.getInterventionDate(),
                i.getCreatedBy(), i.getAssignedTo(), i.getSiteAddress(),
                i.getSiteCity(), i.getEstimatedCost(), i.getTotalCost(),
                i.getClientSignature(), i.getTechnicianSignature(), i.getManagerSignature(), i.getSignedAt(),
                i.getDepartureTime(), i.getArrivalTime(), i.getStartTime(), i.getEndTime(), i.getDurationMinutes(),
                i.getResult(), i.getRecommendations(), i.getBillable(), i.getBillingAmount(), i.getBillingNotes(),
                i.getLocalId(), i.getNotes(), i.getActive(), i.getCreatedAt(), i.getUpdatedAt(), items);
    }
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\service\PdfService.java

``java
package tg.ngstars.interv.service;

import java.io.ByteArrayOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import tg.ngstars.interv.model.Intervention;
import tg.ngstars.interv.model.InterventionItem;

public final class PdfService {

    private PdfService() {}

    public static byte[] generate(Intervention intervention) {
        var baos = new ByteArrayOutputStream();
        var document = new Document();
        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            var titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            var headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            var normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            document.add(new Paragraph("Intervention Report", titleFont));
            document.add(new Paragraph("Reference: " + intervention.getReference(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Client Information", headerFont));
            document.add(new Paragraph("Name: " + intervention.getClientName(), normalFont));
            document.add(new Paragraph("Email: " + intervention.getClientEmail(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Equipment", headerFont));
            document.add(new Paragraph("Type: " + intervention.getEquipmentType(), normalFont));
            document.add(new Paragraph("Brand: " + intervention.getEquipmentBrand(), normalFont));
            document.add(new Paragraph("Model: " + intervention.getEquipmentModel(), normalFont));
            document.add(new Paragraph("Serial: " + intervention.getEquipmentSerial(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Issue & Diagnosis", headerFont));
            document.add(new Paragraph("Reported: " + intervention.getReportedIssue(), normalFont));
            document.add(new Paragraph("Diagnosis: " + intervention.getDiagnosis(), normalFont));
            document.add(new Paragraph(" "));

            if (intervention.getItems() != null && !intervention.getItems().isEmpty()) {
                document.add(new Paragraph("Items", headerFont));
                var table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.addCell("Type");
                table.addCell("Description");
                table.addCell("Qty");
                table.addCell("Unit Price");
                table.addCell("Total");
                for (InterventionItem item : intervention.getItems()) {
                    table.addCell(item.getType());
                    table.addCell(item.getDescription());
                    table.addCell(String.valueOf(item.getQuantity()));
                    table.addCell(item.getUnitPrice().toString());
                    table.addCell(item.getTotal().toString());
                }
                document.add(table);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        } finally {
            document.close();
        }
        return baos.toByteArray();
    }
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\service\PhotoService.java

``java
package tg.ngstars.interv.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tg.ngstars.interv.client.MediaClient;
import tg.ngstars.interv.dto.PhotoResponse;
import tg.ngstars.interv.exception.ForbiddenException;
import tg.ngstars.interv.model.InterventionPhoto;
import tg.ngstars.interv.model.PhotoType;
import tg.ngstars.interv.repository.InterventionPhotoRepository;
import tg.ngstars.interv.repository.InterventionRepository;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PhotoService {

    private static final Logger log = LoggerFactory.getLogger(PhotoService.class);
    private static final int MAX_PHOTOS_PER_CATEGORY = 5;

    private final InterventionPhotoRepository photoRepo;
    private final InterventionRepository interventionRepo;
    private final MediaClient mediaClient;
    private final SecurityUtils securityUtils;

    public PhotoService(
            InterventionPhotoRepository photoRepo,
            InterventionRepository interventionRepo,
            MediaClient mediaClient,
            SecurityUtils securityUtils) {
        this.photoRepo        = photoRepo;
        this.interventionRepo = interventionRepo;
        this.mediaClient      = mediaClient;
        this.securityUtils    = securityUtils;
    }

    // ponytail: JVM-wide lock, per-intervention lock if throughput matters
    @Transactional
    public synchronized PhotoResponse addPhoto(
            UUID interventionId,
            MultipartFile file,
            String type,
            Double latitude,
            Double longitude) throws IOException {

        var intervention = interventionRepo.findById(interventionId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Intervention introuvable : " + interventionId));
        if (!securityUtils.isAdminOrManager() && (intervention.getAssignedTo() == null || !intervention.getAssignedTo().equals(securityUtils.getCurrentUserId())))
            throw new ForbiddenException("Not assigned to this intervention");

        PhotoType photoType = PhotoType.valueOf(type.toUpperCase());

        long count = photoRepo.countByInterventionIdAndType(interventionId, photoType);
        if (count >= MAX_PHOTOS_PER_CATEGORY) {
            throw new IllegalStateException(
                "Limite atteinte : maximum " + MAX_PHOTOS_PER_CATEGORY
                + " photos par catégorie (" + type + ")");
        }

        String url = mediaClient.uploadFile(file);

        InterventionPhoto photo = new InterventionPhoto();
        photo.setIntervention(intervention);
        photo.setUrl(url);
        photo.setType(photoType);
        photo.setLatitude(latitude);
        photo.setLongitude(longitude);
        photo.setTakenAt(Instant.now());
        photo.setOriginalFilename(file.getOriginalFilename());

        try {
            InterventionPhoto saved = photoRepo.save(photo);
            log.info("Photo {} ajoutée à l'intervention {} (total {} {})",
                saved.getId(), interventionId, count + 1, type);
            return PhotoResponse.from(saved);
        } catch (Exception e) {
            mediaClient.deleteFile(extractFilename(url));
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<PhotoResponse> listPhotos(UUID interventionId) {
        return photoRepo.findByInterventionId(interventionId)
            .stream().map(PhotoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PhotoResponse> listPhotosByType(UUID interventionId, String type) {
        PhotoType photoType = PhotoType.valueOf(type.toUpperCase());
        return photoRepo.findByInterventionIdAndType(interventionId, photoType)
            .stream().map(PhotoResponse::from).toList();
    }

    private static String extractFilename(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    @Transactional
    public void deletePhoto(UUID interventionId, UUID photoId) {
        InterventionPhoto photo = photoRepo.findById(photoId)
            .filter(p -> p.getIntervention().getId().equals(interventionId))
            .orElseThrow(() -> new IllegalArgumentException("Photo introuvable : " + photoId));
        var intervention = photo.getIntervention();
        if (!securityUtils.isAdminOrManager() && (intervention.getAssignedTo() == null || !intervention.getAssignedTo().equals(securityUtils.getCurrentUserId())))
            throw new ForbiddenException("Not assigned to this intervention");
        photoRepo.delete(photo);
        log.info("Photo {} supprimée de l'intervention {}", photoId, interventionId);
    }
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\service\SecurityUtils.java

``java
package tg.ngstars.interv.service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public UUID getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Jwt jwt)
                || jwt.getSubject() == null)
            throw new org.springframework.security.authentication.AuthenticationCredentialsNotFoundException("No authenticated user");
        try {
            return UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException e) {
            throw new org.springframework.security.authentication.AuthenticationCredentialsNotFoundException("Invalid user ID in token");
        }
    }

    public Set<String> getCurrentUserRoles() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return Set.of();
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.replace("ROLE_", ""))
                .collect(Collectors.toSet());
    }

    public boolean isAdminOrManager() {
        var roles = getCurrentUserRoles();
        return roles.contains("ADMIN") || roles.contains("MANAGER");
    }
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\service\SignatureService.java

``java
package tg.ngstars.interv.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.ngstars.interv.client.MediaClient;
import tg.ngstars.interv.dto.SignatureRequest;
import tg.ngstars.interv.exception.ForbiddenException;
import tg.ngstars.interv.exception.NotFoundException;
import tg.ngstars.interv.model.Intervention;
import tg.ngstars.interv.repository.InterventionRepository;

import java.io.IOException;
import java.util.UUID;

@Service
public class SignatureService {

    private static final Logger log = LoggerFactory.getLogger(SignatureService.class);

    private final InterventionRepository interventionRepo;
    private final MediaClient mediaClient;
    private final SecurityUtils securityUtils;

    public SignatureService(
            InterventionRepository interventionRepo,
            MediaClient mediaClient,
            SecurityUtils securityUtils) {
        this.interventionRepo = interventionRepo;
        this.mediaClient      = mediaClient;
        this.securityUtils    = securityUtils;
    }

    // ponytail: upload first, then save; cleanup media if save fails
    @Transactional
    public String signClient(UUID interventionId, SignatureRequest req) throws IOException {
        var i = findOrThrow(interventionId);
        if (!securityUtils.isAdminOrManager() && (i.getAssignedTo() == null || !i.getAssignedTo().equals(securityUtils.getCurrentUserId())))
            throw new ForbiddenException("Not assigned to this intervention");
        String url = mediaClient.uploadBase64(req.imageBase64());
        i.setClientSignature(url);
        try {
            interventionRepo.save(i);
        } catch (Exception e) {
            mediaClient.deleteFile(extractFilename(url));
            throw e;
        }
        log.info("Signature CLIENT enregistrée — intervention {}", interventionId);
        return url;
    }

    // ponytail: upload first, then save; cleanup media if save fails
    @Transactional
    public String signTechnician(UUID interventionId, SignatureRequest req) throws IOException {
        var i = findOrThrow(interventionId);
        if (!securityUtils.isAdminOrManager() && (i.getAssignedTo() == null || !i.getAssignedTo().equals(securityUtils.getCurrentUserId())))
            throw new ForbiddenException("Not assigned to this intervention");
        String url = mediaClient.uploadBase64(req.imageBase64());
        i.setTechnicianSignature(url);
        try {
            interventionRepo.save(i);
        } catch (Exception e) {
            mediaClient.deleteFile(extractFilename(url));
            throw e;
        }
        log.info("Signature TECHNICIEN enregistrée — intervention {}", interventionId);
        return url;
    }

    // ponytail: upload first, then save; cleanup media if save fails
    @Transactional
    public String signManager(UUID interventionId, SignatureRequest req) throws IOException {
        var i = findOrThrow(interventionId);
        if (!securityUtils.isAdminOrManager() && (i.getAssignedTo() == null || !i.getAssignedTo().equals(securityUtils.getCurrentUserId())))
            throw new ForbiddenException("Not assigned to this intervention");
        String url = mediaClient.uploadBase64(req.imageBase64());
        i.setManagerSignature(url);

        if (i.getClientSignature() != null
                && i.getTechnicianSignature() != null) {
            i.setStatus("COMPLETED");
            log.info("Intervention {} → COMPLETED (3 signatures présentes)", interventionId);
        }

        try {
            interventionRepo.save(i);
        } catch (Exception e) {
            mediaClient.deleteFile(extractFilename(url));
            throw e;
        }
        log.info("Signature MANAGER enregistrée — intervention {}", interventionId);
        return url;
    }

    private static String extractFilename(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    private Intervention findOrThrow(UUID id) {
        return interventionRepo.findById(id)
            .orElseThrow(() -> new NotFoundException("Intervention not found: " + id));
    }
}

`` 


## intervention-service\src\main\java\tg\ngstars\interv\InterventionServiceApplication.java

``java
package tg.ngstars.interv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InterventionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterventionServiceApplication.class, args);
    }
}

`` 


## intervention-service\src\main\resources\db\migration\V1__init_schema.sql

``sql
CREATE TABLE IF NOT EXISTS interventions (
    id UUID PRIMARY KEY,
    reference VARCHAR(255) NOT NULL UNIQUE,
    client_id UUID NOT NULL,
    client_name VARCHAR(255),
    client_email VARCHAR(255),
    client_phone VARCHAR(255),
    client_address VARCHAR(255),
    equipment_type VARCHAR(255),
    equipment_brand VARCHAR(255),
    equipment_model VARCHAR(255),
    equipment_serial VARCHAR(255),
    reported_issue TEXT,
    diagnosis TEXT,
    status VARCHAR(255) NOT NULL DEFAULT 'PENDING',
    intervention_date TIMESTAMP WITH TIME ZONE,
    created_by UUID,
    assigned_to UUID,
    site_address VARCHAR(255),
    site_city VARCHAR(255),
    estimated_cost NUMERIC(38,2),
    total_cost NUMERIC(38,2),
    client_signature TEXT,
    technician_signature TEXT,
    signed_at TIMESTAMP WITH TIME ZONE,
    notes TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS intervention_items (
    id UUID PRIMARY KEY,
    intervention_id UUID NOT NULL REFERENCES interventions(id),
    type VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price NUMERIC(38,2) NOT NULL DEFAULT 0,
    total NUMERIC(38,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

`` 


## intervention-service\src\main\resources\db\migration\V2__add_photos_and_signatures.sql

``sql
ALTER TABLE interventions ADD COLUMN IF NOT EXISTS manager_signature TEXT;

CREATE TABLE IF NOT EXISTS intervention_photos (
    id UUID PRIMARY KEY,
    intervention_id UUID NOT NULL REFERENCES interventions(id),
    url TEXT NOT NULL,
    type VARCHAR(10) NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    taken_at TIMESTAMP WITH TIME ZONE,
    original_filename VARCHAR(200),
    created_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_photos_intervention_id ON intervention_photos(intervention_id);
CREATE INDEX IF NOT EXISTS idx_photos_intervention_type ON intervention_photos(intervention_id, type);

`` 


## intervention-service\src\main\resources\db\migration\V3__add_intervention_sections.sql

``sql
ALTER TABLE interventions ADD COLUMN IF NOT EXISTS departure_time TIMESTAMP WITH TIME ZONE;
ALTER TABLE interventions ADD COLUMN IF NOT EXISTS arrival_time TIMESTAMP WITH TIME ZONE;
ALTER TABLE interventions ADD COLUMN IF NOT EXISTS start_time TIMESTAMP WITH TIME ZONE;
ALTER TABLE interventions ADD COLUMN IF NOT EXISTS end_time TIMESTAMP WITH TIME ZONE;
ALTER TABLE interventions ADD COLUMN IF NOT EXISTS duration_minutes INTEGER;
ALTER TABLE interventions ADD COLUMN IF NOT EXISTS work_done TEXT;
ALTER TABLE interventions ADD COLUMN IF NOT EXISTS result VARCHAR(20);
ALTER TABLE interventions ADD COLUMN IF NOT EXISTS recommendations TEXT;
ALTER TABLE interventions ADD COLUMN IF NOT EXISTS billable BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE interventions ADD COLUMN IF NOT EXISTS billing_amount NUMERIC(38,2);
ALTER TABLE interventions ADD COLUMN IF NOT EXISTS billing_notes TEXT;
ALTER TABLE interventions ADD COLUMN IF NOT EXISTS local_id VARCHAR(100) UNIQUE;
ALTER TABLE interventions ADD COLUMN IF NOT EXISTS equipment_location VARCHAR(200);
ALTER TABLE interventions ADD COLUMN IF NOT EXISTS openproject_ticket_id VARCHAR(50);
ALTER TABLE interventions ADD COLUMN IF NOT EXISTS openproject_ticket_url VARCHAR(500);

CREATE INDEX IF NOT EXISTS idx_interventions_status ON interventions(status);
CREATE INDEX IF NOT EXISTS idx_interventions_technician ON interventions(assigned_to);
CREATE INDEX IF NOT EXISTS idx_interventions_local_id ON interventions(local_id);

`` 


## intervention-service\src\main\resources\db\migration\V4__add_client_id_index.sql

``sql
CREATE INDEX IF NOT EXISTS idx_interventions_client_id ON interventions(client_id);

`` 


## intervention-service\src\main\resources\application-dev.yml

``yml
spring:
  jpa:
    show-sql: true

logging:
  level:
    tg.ngstars: DEBUG

`` 


## intervention-service\src\main\resources\application-prod.yml

``yml
springdoc:
  swagger-ui:
    enabled: false

logging:
  level:
    tg.ngstars: WARN

`` 


## intervention-service\src\main\resources\application.yml

``yml
server:
  port: 8083
  shutdown: graceful

app:
  cors:
    allowed-origins: ${CORS_ORIGINS:http://localhost:4200,http://localhost:8100}

spring:
  application:
    name: intervention-service
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  lifecycle:
    timeout-per-shutdown-phase: 30s
  config:
    import: optional:file:${ENV_FILE:./.env}[.properties]
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:ng_fields}?currentSchema=intervention
    username: ${DB_USER:ng_fields_user}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        default_schema: intervention
  flyway:
    enabled: true
    schemas: intervention
    locations: classpath:db/migration
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${KEYCLOAK_ISSUER_URI:http://localhost:8088/realms/ng-fields}
  mail:
    host: ${SMTP_HOST:localhost}
    port: ${SMTP_PORT:1025}
    username: ${SMTP_USER:}
    password: ${SMTP_PASSWORD:}

springdoc:
  swagger-ui:
    path: /swagger-ui.html

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus

`` 


## intervention-service\src\test\java\tg\ngstars\interv\service\InterventionServiceTest.java

``java
package tg.ngstars.interv.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tg.ngstars.interv.dto.*;
import tg.ngstars.interv.exception.ForbiddenException;
import tg.ngstars.interv.exception.NotFoundException;
import tg.ngstars.interv.model.Intervention;
import tg.ngstars.interv.model.InterventionItem;
import tg.ngstars.interv.repository.InterventionRepository;

@ExtendWith(MockitoExtension.class)
class InterventionServiceTest {

    @Mock InterventionRepository repo;
    InterventionService service;

    UUID userId = UUID.randomUUID();
    UUID interventionId = UUID.randomUUID();

    Intervention intervention;

    @BeforeEach
    void setUp() {
        service = new InterventionService(repo);
        intervention = Intervention.builder()
                .id(interventionId)
                .reference("INT-001")
                .clientId(UUID.randomUUID())
                .assignedTo(userId)
                .build();
    }

    @Test
    void updateSchedule_shouldSetTimesAndComputeDuration() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var start = OffsetDateTime.parse("2025-01-15T08:00:00Z");
        var end = OffsetDateTime.parse("2025-01-15T10:30:00Z");
        var req = new UpdateScheduleRequest(null, null, start, end);

        var result = service.updateSchedule(interventionId, req, userId, false);

        assertEquals(start, result.startTime());
        assertEquals(end, result.endTime());
        assertEquals(150, result.durationMinutes());
    }

    @Test
    void updateSchedule_whenNotOwner_throwsForbidden() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));

        var otherUser = UUID.randomUUID();
        var req = new UpdateScheduleRequest(null, null, null, null);

        assertThrows(ForbiddenException.class,
                () -> service.updateSchedule(interventionId, req, otherUser, false));
    }

    @Test
    void updateSchedule_adminCanBypassOwnership() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var otherUser = UUID.randomUUID();
        var req = new UpdateScheduleRequest(null, null, null, null);

        assertDoesNotThrow(() -> service.updateSchedule(interventionId, req, otherUser, true));
    }

    @Test
    void getIntervention_notFound_throwsNotFound() {
        when(repo.findById(interventionId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getIntervention(interventionId, userId, false));
    }

    @Test
    void closeIntervention_withAllSignatures_setsCompleted() {
        intervention.setClientSignature("sig-client");
        intervention.setTechnicianSignature("sig-tech");
        intervention.setManagerSignature("sig-mgr");
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.closeIntervention(interventionId, userId, false);

        assertEquals("COMPLETED", result.status());
        assertNotNull(result.signedAt());
    }

    @Test
    void closeIntervention_withoutSignatures_doesNotComplete() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.closeIntervention(interventionId, userId, false);

        assertNotEquals("COMPLETED", result.status());
    }

    @Test
    void syncFromMobile_newIntervention_createsIt() {
        var localId = "local-123";
        var clientId = UUID.randomUUID();
        when(repo.findByLocalId(localId)).thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var req = new SyncRequest("ref-123", clientId, "Client", null, null, null,
                null, null, null, null, null, "PENDING", OffsetDateTime.now(),
                null, null, localId);

        var result = service.syncFromMobile(req, userId, false);

        assertEquals(localId, result.localId());
        assertEquals(clientId, result.clientId());
    }

    @Test
    void syncFromMobile_existingIntervention_updatesIt() {
        var localId = "local-123";
        intervention.setLocalId(localId);
        when(repo.findByLocalId(localId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var req = new SyncRequest("ref-123", UUID.randomUUID(), null, "e@mail.com", "123-456", "Addr 1",
                null, null, null, null, null, "COMPLETED", null, null, null, localId);

        var result = service.syncFromMobile(req, userId, false);

        assertEquals("COMPLETED", result.status());
        assertEquals("e@mail.com", result.clientEmail());
        assertEquals("123-456", result.clientPhone());
        assertEquals("Addr 1", result.clientAddress());
    }

    @Test
    void updateEquipment_shouldSetAllFields() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var req = new UpdateEquipmentRequest("BrandX", "ModelY", "SN123", "Room 1",
                "Won't start", "OP-42", "http://ticket/42");

        var result = service.updateEquipment(interventionId, req, userId, false);

        assertEquals("BrandX", result.equipmentBrand());
        assertEquals("ModelY", result.equipmentModel());
        assertEquals("SN123", result.equipmentSerial());
        assertEquals("Room 1", result.equipmentLocation());
        assertEquals("Won't start", result.reportedIssue());
        assertEquals("OP-42", result.openprojectTicketId());
        assertEquals("http://ticket/42", result.openprojectTicketUrl());
    }

    @Test
    void updateDiagnosis_shouldSetDiagnosisAndWorkDone() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var req = new UpdateDiagnosisRequest("Bad capacitor", "Replaced capacitor");

        var result = service.updateDiagnosis(interventionId, req, userId, false);

        assertEquals("Bad capacitor", result.diagnosis());
        assertEquals("Replaced capacitor", result.workDone());
    }

    @Test
    void updateResult_shouldSetResult() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.updateResult(interventionId, new UpdateResultRequest("COMPLETED"), userId, false);

        assertEquals("COMPLETED", result.result());
    }

    @Test
    void addItem_shouldAddToIntervention() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var req = new ItemRequest("PART", "New capacitor", 2, new BigDecimal("15.00"));

        var result = service.addItem(interventionId, req, userId, false);

        assertEquals(1, result.items().size());
        assertEquals("New capacitor", result.items().getFirst().description());
    }
}

`` 


## media-service\.idea\misc.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="MavenProjectsManager">
    <option name="originalFiles">
      <list>
        <option value="$PROJECT_DIR$/pom.xml" />
      </list>
    </option>
  </component>
  <component name="ProjectRootManager" version="2" languageLevel="JDK_25" default="true" project-jdk-name="25" project-jdk-type="JavaSDK">
    <output url="file://$PROJECT_DIR$/target" />
  </component>
</project>

`` 


## media-service\.idea\modules.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectModuleManager">
    <modules>
      <module fileurl="file://$PROJECT_DIR$/.idea/media-service.iml" filepath="$PROJECT_DIR$/.idea/media-service.iml" />
    </modules>
  </component>
</project>

`` 


## media-service\.idea\vcs.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="VcsDirectoryMappings">
    <mapping directory="$PROJECT_DIR$/../../.." vcs="Git" />
  </component>
</project>

`` 


## media-service\src\main\java\tg\ngstars\media\config\GlobalExceptionHandler.java

``java
package tg.ngstars.media.config;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                fe -> fe.getField(),
                fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalide"
            ));
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Bad Request");
        problem.setProperty("errors", errors);
        problem.setType(URI.create("about:blank"));
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleBadRequest(IllegalArgumentException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("about:blank"));
        return problem;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problem.setTitle("Forbidden");
        problem.setDetail("Access denied");
        problem.setType(URI.create("about:blank"));
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        var problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Internal Server Error");
        problem.setDetail("An unexpected error occurred");
        problem.setType(URI.create("about:blank"));
        return problem;
    }
}

`` 


## media-service\src\main\java\tg\ngstars\media\config\MediaProperties.java

``java
package tg.ngstars.media.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "media")
public record MediaProperties(
    String uploadDir
) {}

`` 


## media-service\src\main\java\tg\ngstars\media\config\SecurityConfig.java

``java
package tg.ngstars.media.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origins:http://localhost:4200,http://localhost:8100}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new RealmRoleConverter());
        return converter;
    }

    static class RealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        @SuppressWarnings("unchecked")
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null) return List.of();
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            if (roles == null) return List.of();
            return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        }
    }
}

`` 


## media-service\src\main\java\tg\ngstars\media\controller\FileController.java

``java
package tg.ngstars.media.controller;

import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.access.prepost.PreAuthorize;
import tg.ngstars.media.service.FileService;

@RestController
@RequestMapping("/api/media")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    private String currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt)
            return jwt.getSubject();
        throw new IllegalStateException("Authenticated user not found");
    }

    public record UploadBase64Request(@NotBlank @Size(max = 10_000_000) String data) {}

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        var filename = fileService.store(file, currentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("filename", filename));
    }

    @PostMapping("/upload-base64")
    public ResponseEntity<Map<String, String>> uploadBase64(@Valid @RequestBody UploadBase64Request body) {
        var data = java.util.Base64.getDecoder().decode(body.data());
        var filename = fileService.storeBytes(data, "png", currentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("filename", filename));
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> download(@PathVariable String filename) {
        var path = fileService.load(filename);
        var resource = new FileSystemResource(path);
        var contentType = determineContentType(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<Void> delete(@PathVariable String filename) {
        fileService.delete(filename, currentUserId());
        return ResponseEntity.noContent().build();
    }

    private static String determineContentType(String filename) {
        var name = filename.toLowerCase();
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".gif")) return "image/gif";
        if (name.endsWith(".pdf")) return "application/pdf";
        return "application/octet-stream";
    }
}

`` 


## media-service\src\main\java\tg\ngstars\media\service\FileService.java

``java
package tg.ngstars.media.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import tg.ngstars.media.config.MediaProperties;

@Service
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    private final MediaProperties mediaProperties;
    private final Path uploadPath;
    private final Path ownershipFile;
    private final ObjectMapper objectMapper;
    private final Map<String, String> fileOwners = new ConcurrentHashMap<>();

    public FileService(MediaProperties mediaProperties, ObjectMapper objectMapper) {
        this.mediaProperties = mediaProperties;
        this.objectMapper = objectMapper;
        this.uploadPath = Path.of(mediaProperties.uploadDir()).toAbsolutePath().normalize();
        this.ownershipFile = uploadPath.resolve(".owners.json");
    }

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(uploadPath);
        if (Files.exists(ownershipFile)) {
            try (var reader = Files.newBufferedReader(ownershipFile)) {
                Map<String, String> loaded = objectMapper.readValue(reader, new TypeReference<Map<String, String>>() {});
                if (loaded != null) fileOwners.putAll(loaded);
            } catch (IOException e) {
                log.warn("Failed to load ownership file, starting fresh", e);
            }
        }
    }

    public String storeBytes(byte[] data, String extension, String userId) {
        var filename = UUID.randomUUID() + "." + extension;
        try {
            var target = uploadPath.resolve(filename);
            Files.write(target, data);
            fileOwners.put(filename, userId);
            persistOwners();
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store bytes", e);
        }
    }

    public String store(MultipartFile file, String userId) {
        var originalName = file.getOriginalFilename();
        var ext = "";
        if (originalName != null && originalName.contains("."))
            ext = originalName.substring(originalName.lastIndexOf('.'));
        var filename = UUID.randomUUID() + ext;

        try (var is = file.getInputStream()) {
            var target = uploadPath.resolve(filename);
            Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
            fileOwners.put(filename, userId);
            persistOwners();
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public Path load(String filename) {
        var file = uploadPath.resolve(filename).normalize();
        if (!file.startsWith(uploadPath))
            throw new SecurityException("Invalid path");
        if (!Files.exists(file))
            throw new IllegalArgumentException("File not found: " + filename);
        return file;
    }

    public void delete(String filename, String userId) {
        var owner = fileOwners.get(filename);
        if (owner != null && !owner.equals(userId))
            throw new SecurityException("Not the owner of this file");
        try {
            Files.deleteIfExists(uploadPath.resolve(filename));
            fileOwners.remove(filename);
            persistOwners();
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    private void persistOwners() {
        try (var writer = Files.newBufferedWriter(ownershipFile)) {
            objectMapper.writeValue(writer, fileOwners);
        } catch (IOException e) {
            log.warn("Failed to persist file ownership", e);
        }
    }
}

`` 


## media-service\src\main\java\tg\ngstars\media\MediaServiceApplication.java

``java
package tg.ngstars.media;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import tg.ngstars.media.config.MediaProperties;

@SpringBootApplication
@EnableConfigurationProperties(MediaProperties.class)
public class MediaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediaServiceApplication.class, args);
    }
}

`` 


## media-service\src\main\resources\application-dev.yml

``yml
logging:
  level:
    tg.ngstars: DEBUG

`` 


## media-service\src\main\resources\application-prod.yml

``yml
springdoc:
  swagger-ui:
    enabled: false
logging:
  level:
    tg.ngstars: WARN

`` 


## media-service\src\main\resources\application.yml

``yml
server:
  port: 8084
  shutdown: graceful

app:
  cors:
    allowed-origins: ${CORS_ORIGINS:http://localhost:4200,http://localhost:8100}

spring:
  application:
    name: media-service
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  lifecycle:
    timeout-per-shutdown-phase: 30s
  config:
    import: optional:file:${ENV_FILE:./.env}[.properties]
  servlet:
    multipart:
      max-file-size: 20MB
      max-request-size: 50MB
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${KEYCLOAK_ISSUER_URI:http://localhost:8088/realms/ng-fields}

media:
  upload-dir: ${MEDIA_UPLOAD_DIR:./uploads}

springdoc:
  swagger-ui:
    path: /swagger-ui.html

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus

`` 


## notification-service\src\main\java\tg\ngstars\notification\config\GlobalExceptionHandler.java

``java
package tg.ngstars.notification.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                fe -> fe.getField(),
                fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalide"
            ));
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Bad Request");
        detail.setProperty("errors", errors);
        return detail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleBadRequest(IllegalArgumentException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setDetail(ex.getMessage());
        return detail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        detail.setDetail("Access denied");
        return detail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex) {
        log.error("Unexpected error", ex);
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        detail.setTitle("Internal Server Error");
        detail.setDetail("An unexpected error occurred");
        return detail;
    }
}

`` 


## notification-service\src\main\java\tg\ngstars\notification\config\SecurityConfig.java

``java
package tg.ngstars.notification.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origins:http://localhost:4200,http://localhost:8100}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new RealmRoleConverter());
        return converter;
    }

    static class RealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        @SuppressWarnings("unchecked")
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null) return List.of();
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            if (roles == null) return List.of();
            return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        }
    }
}

`` 


## notification-service\src\main\java\tg\ngstars\notification\controller\NotificationController.java

``java
package tg.ngstars.notification.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tg.ngstars.notification.dto.EmailRequest;
import tg.ngstars.notification.service.EmailService;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/email")
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody EmailRequest request) {
        emailService.send(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}

`` 


## notification-service\src\main\java\tg\ngstars\notification\dto\EmailRequest.java

``java
package tg.ngstars.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequest(
    @NotBlank @Email String to,
    @NotBlank String subject,
    @NotBlank String template,
    String interventionRef,
    String clientName,
    String equipmentType,
    String status,
    String assignedTo
) {}

`` 


## notification-service\src\main\java\tg\ngstars\notification\service\EmailService.java

``java
package tg.ngstars.notification.service;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import tg.ngstars.notification.dto.EmailRequest;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private static final Set<String> ALLOWED_TEMPLATES = Set.of(
        "intervention-created", "intervention-updated", "intervention-completed", "password-reset");

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    // ponytail: simple retry loop, switch to @Retryable + spring-retry if needed
    public void send(EmailRequest request) {
        var template = request.template();
        if (!ALLOWED_TEMPLATES.contains(template))
            throw new IllegalArgumentException("Template non autorise: " + template);

        var ctx = new Context();
        ctx.setVariables(Map.of(
            "interventionRef", request.interventionRef(),
            "clientName", request.clientName(),
            "equipmentType", request.equipmentType(),
            "status", request.status(),
            "assignedTo", request.assignedTo()
        ));
        var html = templateEngine.process("email/" + template, ctx);

        MessagingException lastException = null;
        var retries = 3;
        for (int attempt = 1; attempt <= retries; attempt++) {
            try {
                var mime = mailSender.createMimeMessage();
                var helper = new MimeMessageHelper(mime, true, "UTF-8");
                helper.setTo(request.to());
                helper.setSubject(request.subject());
                helper.setText(html, true);
                mailSender.send(mime);
                log.info("Email sent to {} subject='{}'", request.to(), request.subject());
                return;
            } catch (MessagingException e) {
                lastException = e;
                if (attempt < retries) {
                    log.warn("Failed to send email (attempt {}/{}): {}", attempt, retries, e.getMessage());
                    try { Thread.sleep(1000L * attempt); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                }
            }
        }
        log.error("Failed to send email to {} after {} attempts", request.to(), retries);
        throw new RuntimeException("Failed to send email after " + retries + " attempts", lastException);
    }
}

`` 


## notification-service\src\main\java\tg\ngstars\notification\NotificationServiceApplication.java

``java
package tg.ngstars.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

}

`` 


## notification-service\src\main\resources\application.yml

``yml
server:
  port: 8085
  shutdown: graceful

app:
  cors:
    allowed-origins: ${CORS_ORIGINS:http://localhost:4200,http://localhost:8100}

spring:
  application:
    name: notification-service
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  lifecycle:
    timeout-per-shutdown-phase: 30s
  config:
    import: optional:file:${ENV_FILE:./.env}[.properties]
  mail:
    host: ${SMTP_HOST:localhost}
    port: ${SMTP_PORT:1025}
    username: ${SMTP_USER:}
    password: ${SMTP_PASSWORD:}
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${KEYCLOAK_ISSUER_URI:http://localhost:8088/realms/ng-fields}

springdoc:
  swagger-ui:
    path: /swagger-ui.html

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus

`` 


## notification-service\src\test\java\tg\ngstars\notification\NotificationServiceApplicationTests.java

``java
package tg.ngstars.notification;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NotificationServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}

`` 


## report-service\src\main\java\tg\ngstars\report\client\InterventionClient.java

``java
package tg.ngstars.report.client;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InterventionClient {

    private final RestClient restClient;

    public InterventionClient(
            @Value("${intervention-service.url:http://localhost:8083}") String baseUrl) {
        var httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        var factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(10));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl + "/api/interventions")
                .requestFactory(factory)
                .requestInterceptor((request, body, execution) -> {
                    var auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                        request.getHeaders().setBearerAuth(jwt.getTokenValue());
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchAll(int size) {
        var page = restClient.get()
                .uri("?size=" + size)
                .retrieve()
                .body(Map.class);
        return (List<Map<String, Object>>) page.get("content");
    }
}

`` 


## report-service\src\main\java\tg\ngstars\report\config\GlobalExceptionHandler.java

``java
package tg.ngstars.report.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleBadRequest(IllegalArgumentException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setDetail(ex.getMessage());
        return detail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        detail.setDetail("Access denied");
        return detail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex) {
        log.error("Unexpected error", ex);
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        detail.setTitle("Internal Server Error");
        detail.setDetail("An unexpected error occurred");
        return detail;
    }
}

`` 


## report-service\src\main\java\tg\ngstars\report\config\SecurityConfig.java

``java
package tg.ngstars.report.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origins:http://localhost:4200,http://localhost:8100}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new RealmRoleConverter());
        return converter;
    }

    static class RealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        @SuppressWarnings("unchecked")
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null) return List.of();
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            if (roles == null) return List.of();
            return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        }
    }
}

`` 


## report-service\src\main\java\tg\ngstars\report\controller\ReportController.java

``java
package tg.ngstars.report.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tg.ngstars.report.service.ReportService;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/interventions/csv")
    public ResponseEntity<byte[]> exportInterventions() {
        var csv = reportService.exportInterventionsCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=interventions.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}

`` 


## report-service\src\main\java\tg\ngstars\report\service\ReportService.java

``java
package tg.ngstars.report.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import tg.ngstars.report.client.InterventionClient;

@Service
public class ReportService {

    private final InterventionClient interventionClient;

    public ReportService(InterventionClient interventionClient) {
        this.interventionClient = interventionClient;
    }

    public byte[] exportInterventionsCsv() {
        var interventions = interventionClient.fetchAll(10000);
        var out = new ByteArrayOutputStream();
        try (var writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
            writer.write("Reference,Client,Email,Telephone,Equipement,Marque,Modele,Probleme,Diagnostic,Travail,Statut,Assignee,Resultat,Facturable,Montant,Cree le,Mis a jour\r\n");
            for (var i : interventions) {
                writer.write(escape(i, "reference"));
                writer.write(",");
                writer.write(escape(i, "clientName"));
                writer.write(",");
                writer.write(escape(i, "clientEmail"));
                writer.write(",");
                writer.write(escape(i, "clientPhone"));
                writer.write(",");
                writer.write(escape(i, "equipmentType"));
                writer.write(",");
                writer.write(escape(i, "equipmentBrand"));
                writer.write(",");
                writer.write(escape(i, "equipmentModel"));
                writer.write(",");
                writer.write(escape(i, "reportedIssue"));
                writer.write(",");
                writer.write(escape(i, "diagnosis"));
                writer.write(",");
                writer.write(escape(i, "workDone"));
                writer.write(",");
                writer.write(escape(i, "status"));
                writer.write(",");
                writer.write(escape(i, "assignedTo"));
                writer.write(",");
                writer.write(escape(i, "result"));
                writer.write(",");
                writer.write(escape(i, "billable"));
                writer.write(",");
                writer.write(escape(i, "billingAmount"));
                writer.write(",");
                writer.write(escape(i, "createdAt"));
                writer.write(",");
                writer.write(escape(i, "updatedAt"));
                writer.write("\r\n");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate CSV", e);
        }
        return out.toByteArray();
    }

    // ponytail: prefix with tab to prevent CSV injection (CWE-1236)
    private static String escape(Map<String, Object> row, String key) {
        var val = row.get(key);
        if (val == null) return "";
        var s = val.toString();
        if (!s.isEmpty() && (s.charAt(0) == '=' || s.charAt(0) == '+' || s.charAt(0) == '-' || s.charAt(0) == '@')) {
            s = "\t" + s;
        }
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}

`` 


## report-service\src\main\java\tg\ngstars\report\ReportServiceApplication.java

``java
package tg.ngstars.report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReportServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportServiceApplication.class, args);
	}

}

`` 


## report-service\src\main\resources\application.yml

``yml
server:
  port: 8086
  shutdown: graceful

app:
  cors:
    allowed-origins: ${CORS_ORIGINS:http://localhost:4200,http://localhost:8100}

spring:
  application:
    name: report-service
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  lifecycle:
    timeout-per-shutdown-phase: 30s
  config:
    import: optional:file:${ENV_FILE:./.env}[.properties]
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${KEYCLOAK_ISSUER_URI:http://localhost:8088/realms/ng-fields}

intervention-service:
  url: ${INTERVENTION_SERVICE_URL:http://localhost:8083}

springdoc:
  swagger-ui:
    path: /swagger-ui.html

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus

`` 


## report-service\src\test\java\tg\ngstars\report\ReportServiceApplicationTests.java

``java
package tg.ngstars.report;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReportServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}

`` 


## .env

``env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=ng_fields
DB_USER=ng_fields_user
DB_PASSWORD=Pg_ng-fields1234
KEYCLOAK_AUTH_SERVER_URL=http://localhost:8088
KEYCLOAK_ADMIN_CLIENT_ID=ng-fields-backend
KEYCLOAK_ADMIN_CLIENT_SECRET=c5c0f83e-d922-42a5-8831-9a054a6ff53c
KEYCLOAK_REALM=ng-fields
KEYCLOAK_ISSUER_URI=http://localhost:8088/realms/ng-fields
SMTP_HOST=localhost
SMTP_PORT=1025
MEDIA_UPLOAD_DIR=./uploads

`` 



---
## POM Files (Maven)


### auth-service\pom.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.1.0</version>
        <relativePath/>
    </parent>

    <groupId>tg.ngstars</groupId>
    <artifactId>auth-service</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>auth-service</name>
    <description>Authentication &amp; User Management Service</description>

    <properties>
        <java.version>25</java.version>
        <springdoc.version>3.0.3</springdoc.version>
        <keycloak.version>26.0.9</keycloak.version>
        
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-flyway</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-database-postgresql</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>
        <dependency>
            <groupId>org.keycloak</groupId>
            <artifactId>keycloak-admin-client</artifactId>
            <version>${keycloak.version}</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

`` 


### client-service\pom.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.1.0</version>
        <relativePath/>
    </parent>
    <groupId>tg.ngstars</groupId>
    <artifactId>client-service</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>client-service</name>
    <description>Client Management Service</description>
    <properties>
        <java.version>25</java.version>
        <springdoc.version>3.0.3</springdoc.version>
        
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-flyway</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-database-postgresql</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

`` 


### gateway-service\pom.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.1.0</version>
        <relativePath/>
    </parent>
    <groupId>tg.ngstars</groupId>
    <artifactId>gateway-service</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>gateway-service</name>
    <description>API Gateway Service</description>
    <properties>
        <java.version>25</java.version>
        <spring-cloud.version>2025.1.2</spring-cloud.version>
        <springdoc.version>3.0.3</springdoc.version>
    </properties>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway-server-webflux</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
        </dependency>
		<dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

`` 


### intervention-service\pom.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.1.0</version>
        <relativePath/>
    </parent>
    <groupId>tg.ngstars</groupId>
    <artifactId>intervention-service</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>intervention-service</name>
    <description>Intervention Management Service</description>
    <properties>
        <java.version>25</java.version>
        <springdoc.version>3.0.3</springdoc.version>
        
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-flyway</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-database-postgresql</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>
        <dependency>
            <groupId>com.github.librepdf</groupId>
            <artifactId>openpdf</artifactId>
            <version>1.4.1</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

`` 


### media-service\pom.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.1.0</version>
        <relativePath/>
    </parent>
    <groupId>tg.ngstars</groupId>
    <artifactId>media-service</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>media-service</name>
    <description>Media &amp; File Management Service</description>
    <properties>
        <java.version>25</java.version>
        <springdoc.version>3.0.3</springdoc.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

`` 


### notification-service\pom.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>4.1.0</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>tg.ngstars</groupId>
	<artifactId>notification-service</artifactId>
	<version>1.0.0-SNAPSHOT</version>
	<name>notification-service</name>
	<description>Notification &amp; Email Service</description>
	<properties>
		<java.version>25</java.version>
		<springdoc.version>3.0.3</springdoc.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-mail</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springdoc</groupId>
			<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
			<version>${springdoc.version}</version>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<excludes>
						<exclude>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
						</exclude>
					</excludes>
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<executions>
					<execution>
						<id>default-compile</id>
						<phase>compile</phase>
						<goals>
							<goal>compile</goal>
						</goals>
						<configuration>
							<annotationProcessorPaths>
								<path>
									<groupId>org.projectlombok</groupId>
									<artifactId>lombok</artifactId>
								</path>
							</annotationProcessorPaths>
						</configuration>
					</execution>
					<execution>
						<id>default-testCompile</id>
						<phase>test-compile</phase>
						<goals>
							<goal>testCompile</goal>
						</goals>
						<configuration>
							<annotationProcessorPaths>
								<path>
									<groupId>org.projectlombok</groupId>
									<artifactId>lombok</artifactId>
								</path>
							</annotationProcessorPaths>
						</configuration>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>

</project>

`` 


### report-service\pom.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>4.1.0</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>tg.ngstars</groupId>
	<artifactId>report-service</artifactId>
	<version>1.0.0-SNAPSHOT</version>
	<name>report-service</name>
	<description>Report Generation Service</description>
	<properties>
		<java.version>25</java.version>
		<springdoc.version>3.0.3</springdoc.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springdoc</groupId>
			<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
			<version>${springdoc.version}</version>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<excludes>
						<exclude>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
						</exclude>
					</excludes>
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<executions>
					<execution>
						<id>default-compile</id>
						<phase>compile</phase>
						<goals>
							<goal>compile</goal>
						</goals>
						<configuration>
							<annotationProcessorPaths>
								<path>
									<groupId>org.projectlombok</groupId>
									<artifactId>lombok</artifactId>
								</path>
							</annotationProcessorPaths>
						</configuration>
					</execution>
					<execution>
						<id>default-testCompile</id>
						<phase>test-compile</phase>
						<goals>
							<goal>testCompile</goal>
						</goals>
						<configuration>
							<annotationProcessorPaths>
								<path>
									<groupId>org.projectlombok</groupId>
									<artifactId>lombok</artifactId>
								</path>
							</annotationProcessorPaths>
						</configuration>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>

</project>

`` 


### pom.xml

``xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.1.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.1.0
         https://maven.apache.org/xsd/maven-4.1.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>tg.ngstars</groupId>
    <artifactId>ng-fields-backend</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <modules>
        <module>auth-service</module>
        <module>client-service</module>
        <module>gateway-service</module>
        <module>intervention-service</module>
        <module>media-service</module>
        <module>notification-service</module>
        <module>report-service</module>
    </modules>

    <properties>
        <java.version>25</java.version>
        <springdoc.version>3.0.3</springdoc.version>
        <spring-cloud.version>2025.1.2</spring-cloud.version>
        <keycloak.version>26.0.9</keycloak.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.springdoc</groupId>
                <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
                <version>${springdoc.version}</version>
            </dependency>
            <dependency>
                <groupId>org.keycloak</groupId>
                <artifactId>keycloak-admin-client</artifactId>
                <version>${keycloak.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
`` 


