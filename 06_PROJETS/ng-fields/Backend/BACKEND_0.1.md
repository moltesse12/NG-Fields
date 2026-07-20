# BACKEND 0.1 - NG-Fields Backend Source Code

| Field | Value |
|-------|-------|
| **Project** | NG-Fields Backend |
| **Architecture** | Java 25 / Spring Boot 4.1.0 / PostgreSQL / Keycloak / Redis |
| **Services** | 6 microservices + shared-lib + API gateway |
| **Generated** | 2026-07-14 |

---

########### Root ###########

## pom.xml

```xml
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
        <module>shared-lib</module>
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
```

## .env.template

```text
# ─────────────────────────────────────────────────────────
# NG-Fields Backend — Variables d'environnement
# Copier ce fichier vers .env et adapter les valeurs
# ─────────────────────────────────────────────────────────

# PostgreSQL
DB_HOST=localhost
DB_PORT=5432
DB_NAME=ng_fields
DB_USER=ng_fields_user
DB_PASSWORD=change_me

# Keycloak
KEYCLOAK_AUTH_SERVER_URL=http://localhost:8088
KEYCLOAK_ADMIN_CLIENT_ID=ng-fields-backend
KEYCLOAK_ADMIN_CLIENT_SECRET=change_me
KEYCLOAK_REALM=ng-fields
KEYCLOAK_ISSUER_URI=http://localhost:8088/realms/ng-fields

# SMTP (MailHog par défaut en dev)
SMTP_HOST=localhost
SMTP_PORT=1025

# Uploads
MEDIA_UPLOAD_DIR=./uploads

```

---

########### shared-lib ###########

## shared-lib\pom.xml

```xml
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
    <artifactId>ng-fields-shared-lib</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>ng-fields-shared-lib</name>
    <description>Shared library for NG-Fields services</description>

    <properties>
        <java.version>25</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
    </dependencies>
</project>

```

## shared-lib\src\main\java\tg\ngstars\common\security\RealmRoleConverter.java

```java
package tg.ngstars.common.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class RealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Object raw = jwt.getClaim("realm_access");
        if (!(raw instanceof Map<?, ?> realmAccess)) return List.of();
        Object rolesObj = realmAccess.get("roles");
        if (!(rolesObj instanceof Collection<?> roles)) return List.of();
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .collect(Collectors.toUnmodifiableList());
    }
}

```

---

########### auth-service ###########

## auth-service\.gitignore

```text
target/
*.class
*.jar
*.war
*.log
*.iml
.idea/
*.swp
*.swo
*~
application-*.yml
!application.yml
!application-dev.yml
!application-prod.yml
.DS_Store

```

## auth-service\hs_err_pid8588.log

```text
#
# There is insufficient memory for the Java Runtime Environment to continue.
# Native memory allocation (mmap) failed to map 134217728 bytes. Error detail: G1 virtual space
# Possible reasons:
#   The system is out of physical RAM or swap space
#   This process is running with CompressedOops enabled, and the Java Heap may be blocking the growth of the native heap
# Possible solutions:
#   Reduce memory load on the system
#   Increase physical memory or swap space
#   Check if swap backing store is full
#   Decrease Java heap size (-Xmx/-Xms)
#   Decrease number of Java threads
#   Decrease Java thread stack sizes (-Xss)
#   Set larger code cache with -XX:ReservedCodeCacheSize=
#   JVM is running with Unscaled Compressed Oops mode in which the Java heap is
#     placed in the first 4GB address space. The Java Heap base address is the
#     maximum limit for the native heap growth. Please use -XX:HeapBaseMinAddress
#     to set the Java Heap base and to place the Java Heap above 4GB virtual address.
# This output file may be truncated or incomplete.
#
#  Out of Memory Error (os_windows.cpp:3554), pid=8588, tid=14496
#
# JRE version:  (25.0.2+10) (build )
# Java VM: Java HotSpot(TM) 64-Bit Server VM (25.0.2+10-LTS-69, mixed mode, sharing, tiered, compressed oops, compressed class ptrs, g1 gc, windows-amd64)
# No core dump will be written. Minidumps are not enabled by default on client versions of Windows
#

---------------  S U M M A R Y ------------

Command Line: C:\Users\FOLLY\AppData\Local\Temp\surefire13481864559330597628\surefirebooter-20260706103051649_3.jar C:\Users\FOLLY\AppData\Local\Temp\surefire13481864559330597628 2026-07-06T10-30-48_857-jvmRun1 surefire-20260706103051649_1tmp surefire_0-20260706103051649_2tmp

Host: Intel(R) Core(TM) i5-6200U CPU @ 2.30GHz, 4 cores, 7G,  Windows 10 , 64 bit Build 19041 (10.0.19041.5915)
Time: Mon Jul  6 10:30:54 2026 Greenwich elapsed time: 0.075439 seconds (0d 0h 0m 0s)

---------------  T H R E A D  ---------------

Current thread (0x000001a298b71e20):  JavaThread "Unknown thread" [_thread_in_vm, id=14496, stack(0x0000004932300000,0x0000004932400000) (1024K)]

Stack: [0x0000004932300000,0x0000004932400000]
Native frames: (J=compiled Java code, j=interpreted, Vv=VM code, C=native code)
V  [jvm.dll+0x7a70f9]  (no source info available)
V  [jvm.dll+0x772d6d]  (no source info available)
V  [jvm.dll+0x965d53]  (no source info available)
V  [jvm.dll+0x968351]  (no source info available)
V  [jvm.dll+0x968a63]  (no source info available)
V  [jvm.dll+0x2fc846]  (no source info available)
V  [jvm.dll+0x7a3325]  (no source info available)
V  [jvm.dll+0x796737]  (no source info available)
V  [jvm.dll+0x3f4988]  (no source info available)
V  [jvm.dll+0x3fc8d6]  (no source info available)
V  [jvm.dll+0x3e8f7e]  (no source info available)
V  [jvm.dll+0x3e9238]  (no source info available)
V  [jvm.dll+0x3c0145]  (no source info available)
V  [jvm.dll+0x3c0dce]  (no source info available)
V  [jvm.dll+0x92cdf7]  (no source info available)
V  [jvm.dll+0x458f02]  (no source info available)
V  [jvm.dll+0x9107a2]  (no source info available)
V  [jvm.dll+0x4f9331]  (no source info available)
V  [jvm.dll+0x4fabc1]  (no source info available)
C  [jli.dll+0x543e]  (no source info available)
C  [ucrtbase.dll+0x21bb2]  (no source info available)
C  [KERNEL32.DLL+0x17374]  (no source info available)
C  [ntdll.dll+0x4cc91]  (no source info available)

Lock stack of current Java thread (top to bottom):


---------------  P R O C E S S  ---------------

Threads class SMR info:
_java_thread_list=0x00007ff974725388, length=0, elements={
}

Java Threads: ( => current thread )
Total: 0

Other Threads:
  0x000001a29ad9ca00 WorkerThread "GC Thread#0"                     [id=18848, stack(0x0000004932400000,0x0000004932500000) (1024K)]
  0x000001a29adb5530 ConcurrentGCThread "G1 Main Marker"            [id=25420, stack(0x0000004932500000,0x0000004932600000) (1024K)]
  0x000001a29adb60d0 WorkerThread "G1 Conc#0"                       [id=15508, stack(0x0000004932600000,0x0000004932700000) (1024K)]

[error occurred during error reporting (printing all threads), id 0xc0000005, EXCEPTION_ACCESS_VIOLATION (0xc0000005) at pc=0x00007ff973e04f1a]
VM state: not at safepoint (not fully initialized)

VM Mutex/Monitor currently owned by a thread:  ([mutex/lock_event])
[0x00007ff9747a9768] Heap_lock - owner thread: 0x000001a298b71e20

Heap address: 0x0000000081e00000, size: 2018 MB, Compressed Oops mode: 32-bit

CDS archive(s) mapped at: [0x0000000000000000-0x0000000000000000-0x0000000000000000), size 0, SharedBaseAddress: 0x0000000800000000, ArchiveRelocationMode: 1.
UseCompressedClassPointers 1, UseCompactObjectHeaders 0
Narrow klass pointer bits 32, Max shift 3
Narrow klass base: 0xffffffffffffffff, Narrow klass shift: -1
Encoding Range: [0xffffffffffffffff - 0x000000007fffffff), (2147483648 bytes)
Klass Range:    [0x0000000000000000 - 0x0000000000000000), (0 bytes)
Klass ID Range:  [4294967295 - 0) (1)
No protection zone.

GC Precious Log:
 CardTable entry size: 512
 Card Set container configuration: InlinePtr #cards 5 size 8 Array Of Cards #cards 10 size 36 Howl #buckets 4 coarsen threshold 1843 Howl Bitmap #cards 512 size 80 coarsen threshold 460 Card regions per heap region 1 cards per card region 2048

Heap:
 garbage-first heap   total reserved 2066432K, committed 0K, used 0K [0x0000000081e00000, 0x0000000100000000)
  region size 1024K, 0 young (0K), 0 survivors (0K)

Heap Regions: E=young(eden), S=young(survivor), O=old, HS=humongous(starts), HC=humongous(continues), CS=collection set, F=free, TAMS=top-at-mark-start, PB=parsable bottom

Card table byte_map: [0x000001a2b29e0000,0x000001a2b2de0000] _byte_map_base: 0x000001a2b25d1000

Marking Bits: (CMBitMap*) 0x000001a29ad9d190
 Bits: [0x000001a2b2de0000, 0x000001a2b4d68000)

GC Heap Usage History (0 events):
No events

Metaspace Usage History (0 events):
No events

Dll operation events (1 events):
Event: 0.024 Loaded shared library C:\Program Files\Java\jdk-25.0.2\bin\java.dll

Deoptimization events (0 events):
No events

Classes loaded (0 events):
No events

Classes unloaded (0 events):
No events

Classes redefined (0 events):
No events

Internal exceptions (0 events):
No events

VM Operations (0 events):
No events

Memory protections (0 events):
No events

Nmethod flushes (0 events):
No events

Events (0 events):
No events


Dynamic libraries:
0x00007ff7d5e00000 - 0x00007ff7d5e10000 	C:\Program Files\Java\jdk-25.0.2\bin\java.exe
0x00007ff9d9f90000 - 0x00007ff9da188000 	C:\WINDOWS\SYSTEM32\ntdll.dll
0x00007ff9d87e0000 - 0x00007ff9d88a2000 	C:\WINDOWS\System32\KERNEL32.DLL
0x00007ff9d7c80000 - 0x00007ff9d7f76000 	C:\WINDOWS\System32\KERNELBASE.dll
0x00007ff9d7b80000 - 0x00007ff9d7c80000 	C:\WINDOWS\System32\ucrtbase.dll
0x00007ff9c39d0000 - 0x00007ff9c39e7000 	C:\Program Files\Java\jdk-25.0.2\bin\jli.dll
0x00007ff9c3510000 - 0x00007ff9c352e000 	C:\Program Files\Java\jdk-25.0.2\bin\VCRUNTIME140.dll
0x00007ff9d9da0000 - 0x00007ff9d9f41000 	C:\WINDOWS\System32\USER32.dll
0x00007ff9d7b50000 - 0x00007ff9d7b72000 	C:\WINDOWS\System32\win32u.dll
0x00007ff9d9620000 - 0x00007ff9d964b000 	C:\WINDOWS\System32\GDI32.dll
0x00007ff9d78e0000 - 0x00007ff9d79f9000 	C:\WINDOWS\System32\gdi32full.dll
0x00007ff9d7a00000 - 0x00007ff9d7a9d000 	C:\WINDOWS\System32\msvcp_win.dll
0x00007ff9bb1f0000 - 0x00007ff9bb48b000 	C:\WINDOWS\WinSxS\amd64_microsoft.windows.common-controls_6595b64144ccf1df_6.0.19041.6456_none_60b8a6cb71f64256\COMCTL32.dll
0x00007ff9d9650000 - 0x00007ff9d96ee000 	C:\WINDOWS\System32\msvcrt.dll
0x00007ff9d8db0000 - 0x00007ff9d8ddf000 	C:\WINDOWS\System32\IMM32.DLL
0x00007ff9cbea0000 - 0x00007ff9cbeac000 	C:\Program Files\Java\jdk-25.0.2\bin\vcruntime140_1.dll
0x00007ff9a1820000 - 0x00007ff9a18ad000 	C:\Program Files\Java\jdk-25.0.2\bin\msvcp140.dll
0x00007ff973a30000 - 0x00007ff97488f000 	C:\Program Files\Java\jdk-25.0.2\bin\server\jvm.dll
0x00007ff9d7fb0000 - 0x00007ff9d8061000 	C:\WINDOWS\System32\ADVAPI32.dll
0x00007ff9d9580000 - 0x00007ff9d961f000 	C:\WINDOWS\System32\sechost.dll
0x00007ff9d88b0000 - 0x00007ff9d89d0000 	C:\WINDOWS\System32\RPCRT4.dll
0x00007ff9d7f80000 - 0x00007ff9d7fa7000 	C:\WINDOWS\System32\bcrypt.dll
0x00007ff9d8df0000 - 0x00007ff9d8e5b000 	C:\WINDOWS\System32\WS2_32.dll
0x00007ff9d7480000 - 0x00007ff9d74cb000 	C:\WINDOWS\SYSTEM32\POWRPROF.dll
0x00007ff9bb550000 - 0x00007ff9bb577000 	C:\WINDOWS\SYSTEM32\WINMM.dll
0x00007ff9cd370000 - 0x00007ff9cd37a000 	C:\WINDOWS\SYSTEM32\VERSION.dll
0x00007ff9d7460000 - 0x00007ff9d7472000 	C:\WINDOWS\SYSTEM32\UMPDC.dll
0x00007ff9d5e30000 - 0x00007ff9d5e42000 	C:\WINDOWS\SYSTEM32\kernel.appcore.dll
0x00007ff9c9120000 - 0x00007ff9c912a000 	C:\Program Files\Java\jdk-25.0.2\bin\jimage.dll
0x00007ff9d5bf0000 - 0x00007ff9d5df1000 	C:\WINDOWS\SYSTEM32\DBGHELP.DLL
0x00007ff9b9e70000 - 0x00007ff9b9ea4000 	C:\WINDOWS\SYSTEM32\dbgcore.DLL
0x00007ff9d7850000 - 0x00007ff9d78d2000 	C:\WINDOWS\System32\bcryptPrimitives.dll
0x00007ff9bee40000 - 0x00007ff9bee5f000 	C:\Program Files\Java\jdk-25.0.2\bin\java.dll
0x00007ff9d9c70000 - 0x00007ff9d9d9b000 	C:\WINDOWS\System32\ole32.dll
0x00007ff9d9120000 - 0x00007ff9d9474000 	C:\WINDOWS\System32\combase.dll
0x00007ff9d8070000 - 0x00007ff9d87e0000 	C:\WINDOWS\System32\SHELL32.dll

JVMTI agents: none

dbghelp: loaded successfully - version: 4.0.5 - missing functions: none
symbol engine: initialized successfully - sym options: 0x614 - pdb path: .;C:\Program Files\Java\jdk-25.0.2\bin;C:\WINDOWS\SYSTEM32;C:\WINDOWS\WinSxS\amd64_microsoft.windows.common-controls_6595b64144ccf1df_6.0.19041.6456_none_60b8a6cb71f64256;C:\Program Files\Java\jdk-25.0.2\bin\server

VM Arguments:
java_command: C:\Users\FOLLY\AppData\Local\Temp\surefire13481864559330597628\surefirebooter-20260706103051649_3.jar C:\Users\FOLLY\AppData\Local\Temp\surefire13481864559330597628 2026-07-06T10-30-48_857-jvmRun1 surefire-20260706103051649_1tmp surefire_0-20260706103051649_2tmp
java_class_path (initial): C:\Users\FOLLY\AppData\Local\Temp\surefire13481864559330597628\surefirebooter-20260706103051649_3.jar
Launcher Type: SUN_STANDARD

[Global flags]
     intx CICompilerCount                          = 3                                         {product} {ergonomic}
     uint ConcGCThreads                            = 1                                         {product} {ergonomic}
     uint G1ConcRefinementThreads                  = 4                                         {product} {ergonomic}
   size_t G1HeapRegionSize                         = 1048576                                   {product} {ergonomic}
   size_t InitialHeapSize                          = 134217728                                 {product} {ergonomic}
   size_t MarkStackSize                            = 4194304                                   {product} {ergonomic}
   size_t MarkStackSizeMax                         = 536870912                                 {product} {ergonomic}
   size_t MaxHeapSize                              = 2116026368                                {product} {ergonomic}
   size_t MinHeapDeltaBytes                        = 1048576                                   {product} {ergonomic}
   size_t MinHeapSize                              = 8388608                                   {product} {ergonomic}
    uintx NonNMethodCodeHeapSize                   = 5832704                                {pd product} {ergonomic}
    uintx NonProfiledCodeHeapSize                  = 122945536                              {pd product} {ergonomic}
    uintx ProfiledCodeHeapSize                     = 122880000                              {pd product} {ergonomic}
    uintx ReservedCodeCacheSize                    = 251658240                              {pd product} {ergonomic}
     bool SegmentedCodeCache                       = true                                      {product} {ergonomic}
   size_t SoftMaxHeapSize                          = 2116026368                             {manageable} {ergonomic}
     bool UseCompressedOops                        = true                           {product lp64_product} {ergonomic}
     bool UseG1GC                                  = true                                      {product} {ergonomic}
     bool UseLargePagesIndividualAllocation        = false                                  {pd product} {ergonomic}

Logging:
Log output configuration:
 #0: stdout all=warning uptime,level,tags foldmultilines=false
 #1: stderr all=off uptime,level,tags foldmultilines=false

Release file:
<release file has not been read>
Environment Variables:
JAVA_HOME=C:\Program Files\Java\jdk-25.0.2
PATH=C:\Python314\Scripts\;C:\Python314\;c:\Users\FOLLY\AppData\Local\Programs\cursor\resources\app\bin;C:\Program Files (x86)\Microsoft\Edge\Application;C:\Program Files\Common Files\Oracle\Java\javapath;C:\Python313\Scripts\;C:\Python313\;C:\Python312\Scripts\;C:\Python312\;C:\Program Files (x86)\VMware\VMware Workstation\bin\;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;C:\ProgramData\chocolatey\bin;C:\Users\FOLLY\.console-ninja\.bin;C:\Users\FOLLY\AppData\Local\Microsoft\WindowsApps;C:\Program Files (x86)\Nmap;C:\Users\FOLLY\AppData\Local\Programs\Ollama;C:\wamp64\bin\php\php8.3.14;C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2024.3.4\bin;C:\Users\FOLLY\.dotnet\tools;C:\Users\FOLLY\AppData\Roaming\npm;C:\Users\FOLLY\AppData\Local\JetBrains\IntelliJ IDEA 2025.1\bin;C:\Users\FOLLY\AppData\Local\GitHubDesktop\bin;c:\Users\FOLLY\AppData\Local\Programs\cursor\resources\app\bin;C:\Program Files\Microsoft SQL Server\170\Tools\Binn\;C:\Program Files\Microsoft SQL Server\Client SDK\ODBC\170\Tools\Binn\;C:\Program Files\Microsoft SQL Server\170\DTS\Binn\;C:\Program Files\Git\cmd;C:\Program Files\MongoDB\Server\8.2\bin;C:\Program Files\mongosh;C:\Program Files\MongoDB\Tools\100\bin;C:\Program Files\dotnet\;C:\Program Files\GitHub CLI\;C:\Program Files\nodejs\;C:\Program Files\Redis\;C:\Users\FOLLY\.local\bin;C:\Users\FOLLY\anaconda3;C:\Users\FOLLY\anaconda3\Library\mingw-w64\bin;C:\Users\FOLLY\anaconda3\Library\usr\bin;C:\Users\FOLLY\anaconda3\Library\bin;C:\Users\FOLLY\anaconda3\Scripts;C:\Users\FOLLY\AppData\Local\Microsoft\WindowsApps;C:\Users\FOLLY\AppData\Local\Programs\Microsoft VS Code\bin;C:\Users\FOLLY\AppData\Local\Programs\Ollama;C:\wamp64\bin\php\php8.3.14;C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2024.3.4\bin;C:\Users\FOLLY\.dotnet\tools;C:\Users\FOLLY\AppData\Local\JetBrains\IntelliJ IDEA 2025.1\bin;C:\Users\FOLLY\AppData\Local\GitHubDesktop\bin;C:\Users\FOLLY\Downloads\flutter\bin;C:\Program Files\JetBrains\PyCharm 2025.1.3.1\bin;C:\Users\FOLLY\.dotnet\tools;C:\Users\FOLLY\.bun\bin;C:\Users\FOLLY\AppData\Local\Programs\mongosh\;C:\Program Files\mongosh\;C:\Users\FOLLY\AppData\Roaming\npm;C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.3\bin;C:\Users\FOLLY\AppData\Local\Microsoft\WinGet\Links;
USERNAME=FOLLY
OS=Windows_NT
PROCESSOR_IDENTIFIER=Intel64 Family 6 Model 78 Stepping 3, GenuineIntel
TMP=C:\Users\FOLLY\AppData\Local\Temp
TEMP=C:\Users\FOLLY\AppData\Local\Temp




Compilation memory statistics disabled.

Periodic native trim disabled

---------------  S Y S T E M  ---------------

OS:
 Windows 10 , 64 bit Build 19041 (10.0.19041.5915)
OS uptime: 18 days 0:02 hours

CPU: total 4 (initial active 4) (2 cores per cpu, 2 threads per core) family 6 model 78 stepping 3 microcode 0xcc, cx8, cmov, fxsr, ht, mmx, 3dnowpref, sse, sse2, sse3, ssse3, sse4.1, sse4.2, popcnt, lzcnt, tsc, tscinvbit, avx, avx2, aes, erms, clmul, bmi1, bmi2, adx, fma, vzeroupper, clflush, clflushopt, rdtscp, f16c
Processor Information for the first 4 processors :
  Max Mhz: 2400, Current Mhz: 2300, Mhz Limit: 2280

Memory: 4k page, system-wide physical 8071M (108M free)
TotalPageFile size 32647M (AvailPageFile size 74M)
current process WorkingSet (physical memory assigned to process): 11M, peak: 11M
current process commit charge ("private bytes"): 52M, peak: 180M

vm_info: Java HotSpot(TM) 64-Bit Server VM (25.0.2+10-LTS-69) for windows-amd64 JRE (25.0.2+10-LTS-69), built on 2025-12-18T11:36:35Z with MS VC++ 17.13 (VS2022)

END.

```

## auth-service\mvnw.cmd

```text
<# : batch portion
@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.4
@REM
@REM Optional ENV vars
@REM   MVNW_REPOURL - repo url base for downloading maven distribution
@REM   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
@REM   MVNW_VERBOSE - true: enable verbose log; others: silence the output
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
@SET PSModulePath=
@FOR /F "usebackq tokens=1* delims==" %%A IN (`powershell -noprofile "& {$scriptDir='%~dp0'; $script='%__MVNW_ARG0_NAME__%'; icm -ScriptBlock ([Scriptblock]::Create((Get-Content -Raw '%~f0'))) -NoNewScope}"`) DO @(
  IF "%%A"=="MVN_CMD" (set __MVNW_CMD__=%%B) ELSE IF "%%B"=="" (echo %%A) ELSE (echo %%A=%%B)
)
@SET PSModulePath=%__MVNW_PSMODULEP_SAVE%
@SET __MVNW_PSMODULEP_SAVE=
@SET __MVNW_ARG0_NAME__=
@SET MVNW_USERNAME=
@SET MVNW_PASSWORD=
@IF NOT "%__MVNW_CMD__%"=="" ("%__MVNW_CMD__%" %*)
@echo Cannot start maven from wrapper >&2 && exit /b 1
@GOTO :EOF
: end batch / begin powershell #>

$ErrorActionPreference = "Stop"
if ($env:MVNW_VERBOSE -eq "true") {
  $VerbosePreference = "Continue"
}

# calculate distributionUrl, requires .mvn/wrapper/maven-wrapper.properties
$distributionUrl = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionUrl
if (!$distributionUrl) {
  Write-Error "cannot read distributionUrl property in $scriptDir/.mvn/wrapper/maven-wrapper.properties"
}

switch -wildcard -casesensitive ( $($distributionUrl -replace '^.*/','') ) {
  "maven-mvnd-*" {
    $USE_MVND = $true
    $distributionUrl = $distributionUrl -replace '-bin\.[^.]*$',"-windows-amd64.zip"
    $MVN_CMD = "mvnd.cmd"
    break
  }
  default {
    $USE_MVND = $false
    $MVN_CMD = $script -replace '^mvnw','mvn'
    break
  }
}

# apply MVNW_REPOURL and calculate MAVEN_HOME
# maven home pattern: ~/.m2/wrapper/dists/{apache-maven-<version>,maven-mvnd-<version>-<platform>}/<hash>
if ($env:MVNW_REPOURL) {
  $MVNW_REPO_PATTERN = if ($USE_MVND -eq $False) { "/org/apache/maven/" } else { "/maven/mvnd/" }
  $distributionUrl = "$env:MVNW_REPOURL$MVNW_REPO_PATTERN$($distributionUrl -replace "^.*$MVNW_REPO_PATTERN",'')"
}
$distributionUrlName = $distributionUrl -replace '^.*/',''
$distributionUrlNameMain = $distributionUrlName -replace '\.[^.]*$','' -replace '-bin$',''

$MAVEN_M2_PATH = "$HOME/.m2"
if ($env:MAVEN_USER_HOME) {
  $MAVEN_M2_PATH = "$env:MAVEN_USER_HOME"
}

if (-not (Test-Path -Path $MAVEN_M2_PATH)) {
    New-Item -Path $MAVEN_M2_PATH -ItemType Directory | Out-Null
}

$MAVEN_WRAPPER_DISTS = $null
if ((Get-Item $MAVEN_M2_PATH).Target[0] -eq $null) {
  $MAVEN_WRAPPER_DISTS = "$MAVEN_M2_PATH/wrapper/dists"
} else {
  $MAVEN_WRAPPER_DISTS = (Get-Item $MAVEN_M2_PATH).Target[0] + "/wrapper/dists"
}

$MAVEN_HOME_PARENT = "$MAVEN_WRAPPER_DISTS/$distributionUrlNameMain"
$MAVEN_HOME_NAME = ([System.Security.Cryptography.SHA256]::Create().ComputeHash([byte[]][char[]]$distributionUrl) | ForEach-Object {$_.ToString("x2")}) -join ''
$MAVEN_HOME = "$MAVEN_HOME_PARENT/$MAVEN_HOME_NAME"

if (Test-Path -Path "$MAVEN_HOME" -PathType Container) {
  Write-Verbose "found existing MAVEN_HOME at $MAVEN_HOME"
  Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"
  exit $?
}

if (! $distributionUrlNameMain -or ($distributionUrlName -eq $distributionUrlNameMain)) {
  Write-Error "distributionUrl is not valid, must end with *-bin.zip, but found $distributionUrl"
}

# prepare tmp dir
$TMP_DOWNLOAD_DIR_HOLDER = New-TemporaryFile
$TMP_DOWNLOAD_DIR = New-Item -Itemtype Directory -Path "$TMP_DOWNLOAD_DIR_HOLDER.dir"
$TMP_DOWNLOAD_DIR_HOLDER.Delete() | Out-Null
trap {
  if ($TMP_DOWNLOAD_DIR.Exists) {
    try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
    catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
  }
}

New-Item -Itemtype Directory -Path "$MAVEN_HOME_PARENT" -Force | Out-Null

# Download and Install Apache Maven
Write-Verbose "Couldn't find MAVEN_HOME, downloading and installing it ..."
Write-Verbose "Downloading from: $distributionUrl"
Write-Verbose "Downloading to: $TMP_DOWNLOAD_DIR/$distributionUrlName"

$webclient = New-Object System.Net.WebClient
if ($env:MVNW_USERNAME -and $env:MVNW_PASSWORD) {
  $webclient.Credentials = New-Object System.Net.NetworkCredential($env:MVNW_USERNAME, $env:MVNW_PASSWORD)
}
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
$webclient.DownloadFile($distributionUrl, "$TMP_DOWNLOAD_DIR/$distributionUrlName") | Out-Null

# If specified, validate the SHA-256 sum of the Maven distribution zip file
$distributionSha256Sum = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionSha256Sum
if ($distributionSha256Sum) {
  if ($USE_MVND) {
    Write-Error "Checksum validation is not supported for maven-mvnd. `nPlease disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties."
  }
  Import-Module $PSHOME\Modules\Microsoft.PowerShell.Utility -Function Get-FileHash
  if ((Get-FileHash "$TMP_DOWNLOAD_DIR/$distributionUrlName" -Algorithm SHA256).Hash.ToLower() -ne $distributionSha256Sum) {
    Write-Error "Error: Failed to validate Maven distribution SHA-256, your Maven distribution might be compromised. If you updated your Maven version, you need to update the specified distributionSha256Sum property."
  }
}

# unzip and move
Expand-Archive "$TMP_DOWNLOAD_DIR/$distributionUrlName" -DestinationPath "$TMP_DOWNLOAD_DIR" | Out-Null

# Find the actual extracted directory name (handles snapshots where filename != directory name)
$actualDistributionDir = ""

# First try the expected directory name (for regular distributions)
$expectedPath = Join-Path "$TMP_DOWNLOAD_DIR" "$distributionUrlNameMain"
$expectedMvnPath = Join-Path "$expectedPath" "bin/$MVN_CMD"
if ((Test-Path -Path $expectedPath -PathType Container) -and (Test-Path -Path $expectedMvnPath -PathType Leaf)) {
  $actualDistributionDir = $distributionUrlNameMain
}

# If not found, search for any directory with the Maven executable (for snapshots)
if (!$actualDistributionDir) {
  Get-ChildItem -Path "$TMP_DOWNLOAD_DIR" -Directory | ForEach-Object {
    $testPath = Join-Path $_.FullName "bin/$MVN_CMD"
    if (Test-Path -Path $testPath -PathType Leaf) {
      $actualDistributionDir = $_.Name
    }
  }
}

if (!$actualDistributionDir) {
  Write-Error "Could not find Maven distribution directory in extracted archive"
}

Write-Verbose "Found extracted Maven distribution directory: $actualDistributionDir"
Rename-Item -Path "$TMP_DOWNLOAD_DIR/$actualDistributionDir" -NewName $MAVEN_HOME_NAME | Out-Null
try {
  Move-Item -Path "$TMP_DOWNLOAD_DIR/$MAVEN_HOME_NAME" -Destination $MAVEN_HOME_PARENT | Out-Null
} catch {
  if (! (Test-Path -Path "$MAVEN_HOME" -PathType Container)) {
    Write-Error "fail to move MAVEN_HOME"
  }
} finally {
  try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
  catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
}

Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"

```

## auth-service\pom.xml

```xml
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
            <groupId>tg.ngstars</groupId>
            <artifactId>ng-fields-shared-lib</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
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

```

## auth-service\.mvn\wrapper\maven-wrapper.jar

```text
PK
    }y�X            	   META-INF/PK   }y�X�1Oe�   J     META-INF/MANIFEST.MF���
�0����]�N� A����Fss�}aA]���|�0��>�=�^Y�!f�<����7����"��VGe eˉ��%-�VIL���V5"�_�VA����s�~ν�)K?7#��P�2'�1*ۆ��k;��H���M�����n�|��ӓ�PK
    }y�X               org/PK
    }y�X               org/apache/PK
    }y�X               org/apache/maven/PK
    }y�X               org/apache/maven/wrapper/PK
    }y�X               org/apache/maven/wrapper/cli/PK
    }y�X               META-INF/maven/PK
    }y�X            (   META-INF/maven/org.apache.maven.wrapper/PK
    }y�X            6   META-INF/maven/org.apache.maven.wrapper/maven-wrapper/PK   }y�X�G�q        META-INF/DEPENDENCIES��A
1E�=E.����N�u���4	i�����v��!?g:�Lʙn�ڤ�*ph�΂F�PJ#[1�!;����V��~^y����ŃU���_߁�����ʦ��k
vGЅ#| PK   }y�X���m  ^,     META-INF/LICENSE�Z[s��~���r�Si��4i牱䆭CiD�n&��%� ���. ���=����dw�VM�5I�ٳ�����E/˝�u�:�^<��?�u�t����B�Mv�������<�h7��W����\�6sc���ʽz���~Z���J��Y]-�7��xws'>��qw}{ws��-~]�SW������	�j.�T�;=�rn��k3�'�	��M#Z%;1�Ie['dW��t���bt�V��Tc�_^>[i7X��{!��pKU��A�U�B��֌۝�N�>hxΔc���X/cO+M�z���w�
P	�� �8�������s+��l��v[z��!S@me#�I�c�$핐%I	Z��Y/��^A�o�i
!�
R����cW��Ҵ��$����a�rxùxg,�я�71ɪ���G3/eFGq�B_�R�W� �Y�*�;�w!#J	N����,`E+;�U�<�׍��+V��N������$ٹe��	�\hЄ��v�GI������%������/i;�a�A���:� �d�A�Fu`�R�+'�3=��6�L\�Z���]�^���&�Q�y|x����[�<�'��$�ְ[	)��GZoU�����kM��[���p4IY�������3�ht�qw�3����r�!8���#A^?P����v��;��Q|�l��p�����cl(?jkZ�����	Q�9|R���o��R�yH\1=��qtLH�^cBR�s� g��'��N����P�n�*-�p��c4��	(��KҘp#-����1b����ZY�<H��M�?å���>�dą�n`x8�[
�dV9X[�BA[/��e��ΰ�Ⓥ�W��#$Sc���
W����h7;� ����$�AP|#:��T�p�~��*܊܅����r��8k� �iՃ&Wb�i|�66|��y6yaX唃H!�K��4��Lou�����NՓ�/ı���0���H��V�R��T��)h:F��j��'2��㤓��N� D��%�"��Ѩ'J�u�����"�����9S6�/�'\��Q6�	�p�H�d�6�
~J�"K�Q���M�m7n ;<x�A�E��z>h#��Z�L���j�De��}���5��i��e�^��f^��˰H5��� 腍l(���uD>��[_`�FW�Ph���d!����R�+��K:"�7@)AZV�"r7���5wTXBJ���	v?V>f+�k�F/2�DAfm�p�rtT�iǖ���ȏ�x�4��`��YC<�Q\��ь����B�M�(P.��#�PD�a�F"��l��"����4���u<v���R�܀���Ѧb�l�PFEHJ���$t���mK��r��7K?����H�p۷���Y��������f&K��TI�H ����� 9�S��� �	���T{�\�3�K��Ǘ�z�'s��pxY[�4�S"��Ts��ᆡۂ�c=��	�%8���+B����@�߀�\j}�E޷�4?b1��ϔs�v�3�J���\�2��`�r�"����K��Y3�]a;�������6u�<��j ~�Q��1<Q���`&�M�>
�ʾo��48�����U+����lv8�"	ɭq���uNZM�Y[@���(j_����`�)_���DVOˎ�q��-��$o���b���n.�5�?�B�
c::e�[VAn%�L ���T�"��ƹ�d0<FiF�O�</E#�n��Q[.`��|�G���QM`ŝo���29����S1LŦ�(ShF}��F#�/y�Uqu�E�X�.�
���ҰO�
���;�O��u+	َQpPn3��gX�i#l6�Q!���7�"O�f.�O Y�Z!2H
�V)�rm艸��z�셼䓎i[���~ܪ�Z9���!��TR}8�$��2��d{��&Qi죰硎���Aw'�=�l{���([�-C����e��U$Xxs��Sw .�8n���Kձ��] ,V
yS��	
�!��?� ��s�����g�A�U�-T<&��3��p�INK��h�%�V�o��ճ��������q {c��=�rg��ٕA��L9�,�+ZO	>���)��Y�"(I��fb<�2�A�ŗ�5s��g�J�2%�S���/I�
�6}ԔA�d�d�IT�gu�>�I��y=@	]'����M�T��ũ�e�zٔ��g�Te
� �Y �V/���s�0#�P���wa�_�f��M�[�8�"5��P����"�:Lf�lȪ�[�w�̤ս��$
��G�g�~
�U��jlm�DL ���;�1��`���D�*虘��8��0O�[�5Q�*��Ұ�	���+s
���UƑ�F�:a�g|흹2b1�]���hS����Y<<ъ�ӹ�J$�ΦyI��۪I��g�D�1�&c�ةu�|K͎�	�^5�@7:�����a�Rc�K��8�8��l�����]%��;r��m���Ӛy�Ejf�"��V���ׯ̀���՗���vK��R͍P��_ad.�1��)X1�D[��(�>C�#S��� ��7Ī��|�t�{���?�3]B΁)wv#���jL_�5�lqnN��}����:��C��C��6ժ�F�o���;�	�tr)~���4jV�Q��+bӁ�ړ�lȦ�7_Δ �ԟ��J;j��Ҷ��]1	���7��yc��`��H�K���a>�]R�uš�q��?��ˉs/q��?[��r=?,��u0�����7������bu��^����Z��X�~_����h�~��K'ф+U6&MDsRp� M.��"{
�`��������z�\��[��z����?]߽��\��|����B���~u���^�������N�~���Y_s����o@�6�t�@73�N�<gMo5�s:pх�P�%���<mt87��v��Δ:���������E�i3˱��9|&�E���.ϗXyПn =X|�аt�N;���,�!tj�h`_��,�mw1����g�������!BG�mq�-����v�|~0zN�e��M�� �V�r;�����J@z9��
�ֳ�gH( �|���g�x!��ƙ��j�w�X�c��[��F��9F���ygf��O.��Z���5���&�~��l�^�9����R7��j$�z���"x�M������+��q��x�e�a��4]����� o��r����\,J�	h�����"�,)>O��������B˝1<�I�䲝f���jExPGʮT|��Ǡ�w���Ւ4c�6Awa6��Boy���̗�Z�<�/���Ac���c'ĭd4�3��Go�tMv9���!���4�(�KL'ݢ$DO��,�L{&]3>c�s��m�h�J�Ю�
`�ՙѹ�-!Q �ъ)�Gk�m��&CW��*Q�ӹ����F:�-�l��>�ƌ6F]8��WWXWϽG�/no��?ߠiZ �z�/���o��>�%���.(�k�iB����B>��F�:�Z��r
$;��o)D��_g�h2��!�����Nz..�L����@��A��.u�Ԧ:�	@��;��vv7������"��zV p6/��i?'(N�r�@�!c嶋hf�q�Zݨ��
ݐM.��r4�F�a���|��_PM<���½kϤ!���o�9�e�/��U�Bz��G�����>H��g��O��*.������("�#\���<�x��6��1FT�8"u�fC�29م@�C�Ͻr���j}�T�%_�П���3���N�p�K������H��&�������h��ێp@	�,t�o��iI����\��PK   }y�X��w��   �      META-INF/NOTICE}̱
�0��=Oq���:���Ap��|4g(��rm�����_>����(B
WTwayi��A�����0D����ɝ�VQ	z^r@K��sCLD9,�A��*P~6�J3@s�g��frj�Z��/�S���PK   }y�X�۱A�  U  3   org/apache/maven/wrapper/BootstrapMainStarter.class�Wk{�~ǖ���	�pJ��M�eǖJ 4�!B��vj�R��t%��M�]e����Ҧ)�׶h�Wz/��/��~���~)}g�U��:<�#��9��9������G �G1t�SAHE�m�-S�̹�d�^t"#�i8�t��O�E����n�C�=�ѪZ��g���,�Z��ۙ�,˩9��k��w4��m��f����&�c)����iX�Y��gNhNy�?��`����ɜ�ʍV�Z-gi%�V�M@m_�F�+�Q�;�5
��m�픷�+;6ÝH(إb7��+k�M�6��(v�U�T�|V`���l3U�gNw�j�>�#'<u�%\��S�+�vQ��f�������S3~S6�$C8��
U!��ɲ�k%�-ź-�{��]�T�y�c���Ψe:���shWjS+�xP�~�O�h��V���}�CQ�-Zv�TKW����rF����/� ��P��U��}��c���0���
��DM��y]�Ȉ������X�5�Rw�Fn�Lf��yT�a<&u��^�1jZ��xB�O��F����	�b�8�xqk���(�Q};rE˜��9�]0���>� m�P1�����] �9v~ף�ͳ��d1��W�i��g�`��K*�q����S��W�l<�!h����l�M)��@�O�i_Vq_�s3F61�\�.�ك�{6Z�o�&�#)��/��)(y�� ��ɪcXf��SPVa�<�����m�7l��l�}_`��m��p��(*��Q2Cm�H��li���B����[{e]c�=����:E�������o�͂1xh���W�1*��&�
�pI�WU|_������[�j�5�tVJ|Cųx��1]����~Sŷ�1.V�����^|��f-j���|G��x��P+���j��R�e�S� 	v4�� ��2��sĪ��'����.|?P𒊗��\�rVQ�0=CS��,�xn���5��Q�^)%M�IVHv�S֓㲡%�^�<vx*i�Mj��)l�r�d_o�/��#?V����R��Ef-{^#��S�u��O�	����a�6ϋ�����Y���L�'���>)��x��Ok�!�Mb�),%��\B���|�w��fx��i�w�rw8�Ãi��$wY��M�p*&|u���7�晶}C+t/��ڮ�;#��٥�[�z�#�Hͭ,e=ો�JK�_�%�{��
<��i$Pi��[��Tn�^�0�U]����"��d�_��U����Z((o�R�׺(/��B^���	g��cx`�:_:�S>#.���6�.~�Q����(,�ɧ�vy`p��l���mӝCk�.��;�{{Vp_6���Dh�D(��2�g��k��k8$���3����5�w�OO��_���tg�T~g��DD��1ݹ��3�]�����Ѱ���x���^^���}&�/3��\��s��:z��L�Q��K�v���_��t:����:��)��E�Z��w�=��m��H�?5ak���k�"�:���	�EF%�{����`�MW�_�Ӄ���� ���Dh��p�|{F��g�C�D9.3���9�kL��=��;���'B��2�&B�����{�W=W��
���V��A^z���s�!�4m���=Ȓk{0BT&�F9���+�;�7Z�<{x���Zvs�����-��������E",��ɱ!�Ғ\���!�	��(�*��?~B-��7:r
�|���ׯ@�%C+���n���/PK   }y�X܇�H  C  2   org/apache/maven/wrapper/DefaultDownloader$1.class�TmOA~
�ak�"��R����O5��Zb[�m��.��.�^!|�'��h��Gg�HP �Knvfn�yfgg����?<�3CY��.��͎p�|OHw_��]�ݲ���}9�F,�:�0ư|.� vx?j_���N�1����^%\����������+E���aG��o�Pi��ܗ~��0���-$f�C����W�u�+9�$�d�^i�[+nV�墍똲�r0��(C�eۭ�B�%����cXȔN|�/۹�i�n�%��k���͂�۸cᮃY��Ka&B�u��k�b��g#�����gPoy�:���Lk"橖`��|)*���o�v@�dI5yP��7��3f��a�,��,jU��]꟦3�ȳυa>s������5��M��7��N�����l�G=�`�Fq���;xb�n�����k��Ά�B{�)z��+b��b���$��;�4�H[��x�ť/^<���(f��(�xH25�BY �L6���
�� �%,�ˤ}E�O�@�]��7\Kΐ��[����!Ia�GLL�H��El� |�6�Gb.�1��1�,L,���PK   }y�X�4'0  �  S   org/apache/maven/wrapper/DefaultDownloader$SystemPropertiesProxyAuthenticator.class�SkkA=7M�q�>\5��Z�Z�X��VQ"BI�TՂ��4��d7�N��W���?J��Y��pv��9���wf~����2��H`�B2�a��UwE[��m�]�{J��R����4�j��7�#���A�ek]��=r�����k�&t�#/<��/	C�����ѺD�|����v��-�����E�j�������ŶQ|Jec�.���
a2�h
���LF�R�V��)�V�_/�ϧR�FpN��J!K��2�7��^H��-ܶp'�i̜uع*�V��<��oUl֞5;6G��Qު�7Z	v$a����M��-�;���Sj��)�g�q2�^Hx[��'�L�2�ﻓ��\ܒ�o73�r��*���'������F�Q5��3ɜ��WB���KUi�_ZxD(\\�0��YxBX�<f�"�`�!��2�0�3�G>�.���A��;%�b��� ���1�<
q�g�Z�;�N��q窓���!n���c:�ˀC�=���p|B��QO�1�s�/��+�ľL���QY1»X�����/�r��oPK   }y�X�3�    0   org/apache/maven/wrapper/DefaultDownloader.class�Y	`י��-k�bB@lAJ����C.;68��W-cc �A�yF� w��&i�6=��^��3i�*��p�݋�v�{�g��}d�nw�f�73�%[N	�y�~���+ v�c
�Z-{&���Ԭ���Ofⴭ�r����Io0�g�hPԠ@XsB?�'��9�9~�H9�{3fƹ_���u�!�R�i���V�ט�Yg�u��Zzڰn�J6�ҝ�e�s�@xp�]ұ3�L�d�F�ZI=\q`°�|��ѰN�zS�2�33���3�g�0�ܸAC���T�X�GET`�����9I�@ްC؄��E�f�\e��|�1�V�RIg^`G�rZ�O)�E`��[k����1�KU�(i[�۶	�l��;{Z'B؁[�hhE��Wb�pU<By��)l�]��Ut�041<y�@�l�w�_�2�!!��󔊝�]�������^�t\��!t������ќb*�[���"c%�3Y#1�1O��<�t���W�} �#���O��g2y�/]%s�6���l�������Z���UЧa����4J��6�K������A����C�/8�����Ti���X���1�flF�eg�6coJ���ԔNŠ@���|�Q0�����906�i���d.X�R#lB�&q�:����,�X_�^Ux�@�{c�˘y#�<cF0��N�9V,�#$FH��K�Ů����f=��g��X�%�o||4�QpT�1�M���8h���<8����K�cB��RHSݒ\�cئ���+"�&�e~Id�f@458ց1�cMI��A��N*�j��Y�n_��c�)[I`��k=R朆GA�6��t��Ϥ�S-KRmɗ�L�h(H��Sz6U��29�ΐ���LKr��C���|����;*3Y]�b&#Z�|E����j�E�;YG�.�2ٴ���AF���a-K��Y���S�ƻ	V	����e|}��
����>I��K�XvJB� >���~gY��}�v6AD3d�I��29�-Z|L7�K�⯱З����\f?��#���jg�AK*���!|�PpNÏ�G��Ey�=k�UF،i�K�e����S���3��F����H����K_|N����`y+{��ee�U�D�s��/QF�r��0tڤ�d	�X`J��e�e����R#~
?�����y�X���C��vzq?m1�?:ػ��X�������C[��X���_��U�@��8/�PKMy�L֯ ,�׍��"I�YҢS5k��R��K��5\�\��Lm���7|����,i�֏gyW�s~����i)Y����� l��B���|�qM���%�>mdٵL��������d����_���[���i��[��j"��)�L���o����d�p�iLr�m�#[)�8kѝ��͔�6d��{~@��{�z��F���;I���?R����v|ɺ�u�u��أ˩xK���r�!���7�
�R�_��5ϲ�˚������]��2�S<Oi[������5C�F��A��2j��mc:���(��?�Q\��6�����w0���.Pw�/���*�/��o�2Ati�<e�d��S�
~���O5���_���;���-eT�fu�un15xc��������n\����v[�i,5��R߼#;��-+i=ܧ�����?�2��VE�������e��3f:�Wwtg>g�2�b	�M� �є�g쾌���h���T�
�%�
���Z{ٍ-����g�m�b^U7%�L>fZN,��`Q�t�s�V������*n�-C�{��	�	�1���b-o�-{^�(��&���%�R�P�N��Bi�j��3N��`Ul��7~jnq�&V;+7�u�b3���b�[���l��"�*b�&�����-�n'2c##���y�R�*ZxܞOl��Z��{��[+^	"Ѧ�v��`pڲ�t��,�����bSԣ���֕/}_{���`�pa�a�K/IŤ���c2��f���P2g3�R'��OE-5�7��,8�����M�%нZ��VY���h'	5c������-V��.�5���&�4��q�H��\O�قk
c.�����w�,%KU4�j�\��,��R�۸r�%��\ZګxC��ː�_�c����lF�Z?X#�6�U�1XBD�
��5^���t?N�9.��l����PH
nd�qZ^Z��T�Zy�cW�����e �X��&e���.���i�Z�v�	y��-�����CX~��\�>AS�\Mp��z>��.����4�u��9
����T'n��F�� ��Hb'Iu���؀]b'Wb�%n�����d\���A�َp�\[n^��kX}	�)q[�]�wy�vi��Sw#�n������]r/D�ě��g|����輆�������˒Y���U�l�{-w?{ ͼFl�@�m�L���H�>��YNIi�:�/���	��x\�ѿ��&��1TD������w\��(�f:�*��5���c؁$�1A�OVع�l�.q����K�-�4R� �͢�}B�>U�T�JO���Jcw����Ga�(��h |*�P�O�Kxl�"~����xW�=E<���C��ß�O?�">[��`4Pĳ�JT����S���\8vTN'��5�*E��6w�r��jݍQ5�X��Fի��~q�9��y������Q<���U�6��|N�K�@�.�
t�l�1�8D���G��G�ܣ��atb�8�}F������@Ӥ=C�iҜŗ(�|���8�zb��;��E����ە�O��}r��	y�(���w����M���%��uF�q_�������� �`X�n'�+w�/A(��6�����&�ۨ��g���=��Q�~�|��|���_�/w�,!�5���ߠ[�:/�<�K�e�P21�z-�]ôyD��h-��U�S�:��*���*̸���w�q���	'�iT�rζ�E��PG�ϊ�F�oN��w����!�^���[��݁N�u"�U��hS���?E|�;^���wD�|#"�Շ�Q�@w�P�""�(�Uʩ��4��O�I>=�t1l�wr�q��	l��]�OѭOs�{���$����8�~�bG�R�����83� �<��ô�I4�sO�1(���dt�M&��ek�u-�
�gĈ�B;�^��.d�o �S�!P��;K�x��1r�#�Νn2rS�p�5�̄����5�Eqc�����E>�+�7?J�|���qt�\Vv���)��b%�v1��E���U�E�+��K�:yr~�GT>O�}�-_D�g���&>�#"�Gw "6�gCD�E>�q�|*�-q��u=�7&��ɩ�hCr*&��̒SjGrjM��?J4�\����q���S��Ӑ��"�,}�9֏���_`�|��2(��m�Ÿ�+��W��	��m¨��[�T���n�h�n1ŷ )�-�M���aW�#���ױD��Q�'q��?PK   }y�X�y]�   �   )   org/apache/maven/wrapper/Downloader.classE��j�P����

>���lܹm���~LF�����u��C�%��a�p������Z��u'�B�D�,5\9)
u�l+�[���F�\�s1'~?d��~\#��l��-y�y[�*�|Ls嵔�r�'�/_�ej�G�¸�zw<���.�WJ6A�nt�@h�V�su��:��g�w���PK   }y�XK>8ڤ  {
  4   org/apache/maven/wrapper/HashAlgorithmVerifier.class�V]wE~���4]�J�V`)��Z%A��@J��B���d�lIv�f��
~r��x���xQzD�o��x<G~�zA}gӔ����vfv�y����y���x��h��ï � C�>�Ǌ����LN���<j��s�����B#GXA�^�6��ذ�T�8e�E�$���I�G73��`$�Lyڱ3���^� ���a��0+�3�"6R�H�1,3�fl�hQ�[����Pa�h���Yv2(��В���SHL�ֳRs�NV�I��m��h�_�a�ӔeD&�I��K
v`'Qꖳ�C�p:J&%�۠)�-S�����SS�6Z��}�(��D��}�ϰA�f�n�lm�,m_I�D������&�٥��#щA�^���3�Ŭ���ALVF�D��!�UW��a*�W8��׈�gg�Ȧ��+FrDd0�Z}L��A�=��u[UuU��eβK:yy�N�L��/�Dt-Jz���$uz�RJ��_�k���<��4���e:�tN_w�"�V�JM'���]A���^i�$9�*8�T->�1����ыď����(CK���D��czѐ5�պ*ZA�4"D�K���Nh��JT�xA��ù�Zu���4F9�V0�K�,;��z� b%}Z��[rj��|^��a�,��z�q�3�?;�蚎Q�od�w�B�`�nH�˗�!N/��K[Z֨��IWB��m�r�zg%#_p$A�T���A��OK椀V�uٕ��®���P3E�x�9�U�"c�jH!����q��{q��=��u�9C�;Q�[�Jc�l�a��251z��R�lJ�8�&�=*ﳬ��1�6��Ҧ�)T�����M�.Ǵ\�2�}�����D�H���H&�,
3�X�z���l����[�W_F�~���z.2�j�Y�S+�ah�k��mq��J��v�k�{ٕ�O*�8t�<u������g��e/-S�Ok�f/����U���	��T�1�E�^7�}R���Uj��Sg�35t'�+F3�9p��]�8Cc��܉,�J ��yE�q�
�=�] ��}�a����~
ŹT�<Z�B_�Y����G����j�1���y��N�|��K�J<�r�p{U�H�#�Gϸ|(�C�D$�p��7n�6~��j�5|x�I�-��0�kmZ���������a6�������?�͖᠇�M������O�\�î��<.!Wːyz�<�K�7��S��7�<�>��A�}��V{�F���	%�\�&��5�;���4K	�������m老)pJ�f\C��n�Y�`��;tn�L?�������u�^������+�V>o5M+?I�F~�P>u|�d#H�na��r|H�,�O�/��6���=�ѹ��r�*�-�x����!�����&G����"��,���O��3��>�PK   }y�XXW1�  *  *   org/apache/maven/wrapper/Installer$1.class�UISA��$$�Q�QԈ��w��X��@��lghB�0��� ��'V��Ej�KyPO�Q��;!@����^��}�_�y��ǧ� �b,�D√���!��e�W�� �%�,\��+�[y7��?3��.� s���rH�a�h��px�/s˕�5/a�ʥ�#nҲ(z>��HW����l�@
q�M�@'�΍��/ �9�P0��+l4���B.[Lb���mb�2toF��wDPuB�������Խ	
��[�\�:7��ك&�q��/������ar�Pk2�'VlQ	���MbP\=&��(CrY)�C�7C_������@��slU4�f8ѷ�"����E�Q����.����T����N��R��¨*2��m�g�)��m.xs3��KT>�F�$�Ӯ��@J�b3tm�p��ِ�n�J=���^�Ín��{L����(�����p�lM��T�� ���]|rV�]V}:�B��zd�K%c֫���l�q�'�9�ʄk;^ �r�v��p�yE����E��n�20�+�q���C�L�z K�U�U��6#y�V�4��l������ڵ�8��`4>A/��kV8�>@�bZ�U�d�8MjuXN�ͩ��h��������$݉(�Oi|F�='�����ՠI�:�1D�'53`�"J�S�RG�O։��NY;��O�|^��J4�8]�J��3Z��ct�sd�������}ž��8�Ʊ>c�^�fl-Z��$���z	���
��@��v��Kz���6�[����	=?PK   }y�X[/A�  �#  (   org/apache/maven/wrapper/Installer.class�Z`Tՙ��d�;3��Bȃ"�Y�	IPz��$�����`�[�V��[��Jڊ�<d�nkպ�v������vw����]�[�߹��$���*s�=�?���������z�E ��y����Q�B��}��&d��k���gvڂµ�p�^'�/_��^E���hw�1:{̚^������i
�l#2�w���,# 8�9熆$��S�L�13���v�Ź7�'����:J0[0-b�=���ٻב�<�����u^�a���:|8CP�sێ�s��
vǣ��ݦ������xbff��fǰaӮپ��9�,�0���X}���5��DK�T$~����0�د��X��s���c�:oMk�W0;u���V���y�2�kX���9|<�@�{#��U�ְBY�F�tj�����V�|�~Y�lu�Le��J��p���8Op���H�w#C��,��U��)g��O��c����["���VC����PP2�N1�	<a�n����*�vȮ�\�-ߩ��s��l}<b�x��n��Of������e���AG#.fv+��s���eb�$�r�QG��z�lF��	�6�hq(&�ɠ����1+�M5��ڪ��l�,	�T�uh�Ѧ��m[�E/ڱC��::�S� gd4[��*l]�p�%(�f�v7v�T�*�*�ԱG�WD(������`EĈڊ�SG ���Q��0[�{CN���6��xPaխ�Ar	�!�6��R!4'k-�Y��i�U0��^<�%8������5D&$�+2��Q��i�u�q@�K�j�����5nm��и���ֶ��KXU�?j�c�~8H��Zh⎬��J�e���O���J�B�1��u�Z��z�\�3z�civ$&�!�s̮+
�o�GǮ�qC0��5�*X��Ѝ�	�뫹���:���(�SH�'�Sf4Nefy�R�n��^"h��t+����jM�y*~>��~��S<�������V��u��tq��+Z��ѶAe�J�@0�Y+:�W>zXǗ�q7�9�RΤa��u`{�`0Q��x�'�m��tT���u�v\*�n*g�)8cT�m��5�;�d�<�oi����p,U�A�*�۵m˖6�y��FpE�_vvl�?`�1?k���
�F0�7��&Z�o�ph��]�p�o��vh��2�J����èˊ��Ŧ�L�]Sr&�70�:Н*����s��.��� �)�ӕGN�����ܘɚjv�m��}���b�@L1�����%0<h:�U�/v�X�P1Z���f�+hƦh0�� �<�Ѩ1��ۋW����/u����C�f_Cj�al�ΛEғ�1�~��5��`ޤ���7t��B��`,��������Oh&#WS�۩�yK�?��BAgȊ�~�R�!m�Z}�^��?��'���8Y��#I�s3��w0Y�k��
 <���ꋹ񯄆��pM��o�����Z����羅��G��^����?u��T�5�a'�N�Z�`�*�qC�Z���xq�R�x�մ��{���!�0���`�H���z�o�Ĝoڒ�7�OP����N��l�d*�������Z��{�sW���L#f�vO0�=Άq���Xq��U��I�d�
�P}��C�M���(O>M�[�a�=^��$_���V�l�7�$m�Z�-��w���=)�.n��y�[	D�եHx�,��f��w	�1�/���s�t�ܗ)�m�.����+���33E��cD[ͫ�f��T���^��/�KVz�D�i2G�l�Qz���O�3�L6�}Fh�
���I��Rmy{0T�6k1od�.�U�ʛ�<DT���\5�[M�*�6,�e�,!��V�v��͒�����Y�t]&�U>v�Zqu�.RIӛa[��7�Ah�{��u�H�e�&5��� �4+o�l6[Q�1d��kLm[�˹�](R�BrA*[6A�F
2���	]�W7��x�r�.u����7Q�d�hra���a��C��lg��pg(`�5���Y$�i�~��ň:t�����]w��0���l��΅Wʨ��70M�\*�h�N���ᤊ�`۔n�9�����ͺ��6���)�۩�U���������
5�2]�I+1��]�������+��&홨9ʏZ�-�t(0H$�[v����^��+5��6D>9�"���i� خ��6VpܲW�Na�54^\���mOo?ۚ�ook�Ҳgk}�F���6b��I�W�)|-����4�k�`��h�͖x�^3ڦ*��IܶѠzON�TU!TN��g�؍��}��GY��{#�/:���O�����c�D�3����`쥙Ƹ�y���Xc���f�&~����?��t�a���YՇi���>8wؙ�o��ϱ.��ܼt��l�XG�x��ܿو85��BeڍB��4���c���
e�r4�P��,�������9����Æ��ĵS���dU���J������KG1o��S8��h��Fw��^�jtۺ���<i��HTh�T�K�)3Qˉ�h�3�;NY�4+4S9����υ��DI՘ �����ԃ��3���N0��۷��Vb���]�����\2)��v����m���N3q��48�P�i��p�`2�f��	I�����p�����b�W$�sf:����ZLL�t�-�].>gWA**��VQ��UT?�Y�8�'��-���b�P�0k��B��lEb�e�?��s����:;�g���Nο��L��A�n�ϙ���`������4�Q���T<�Y���-��Zm��.u��������Xt�:�pQq� 6(�KFp�`[�m��׸�ݱ/�cwT�	}Zrާ��
�C�'I����C���F�J�U�<#ؗ�ǧ�^�Ց�4�jFߎ�9W�q;��z*|�!\S�-uA��~��;�a�^Zped8ͧ�\|���>[ Eq���W4�C�t�}A�l��S
|Q���շ§ᡣ�_M}Ei)��Gիg�����)Q��u��xO?M-�:�}*���OT���#x>O	4�Վ�`z<<����d�xJE�aa/b���7��F��%��F�¥m�v4���.lA[1��p��Z1�6|�o��st�m���{��Ӎn\)%�+e|������ގ.ٍnك������'G�_EH^@T^�-?A\~��N���x=��e~y�a��9�U���#K"N��A��(��+���،����s��B9)8*@��H���X%�� �i���oK�\�N�oe�|J>Mn���_��k9sg�`�)���z���o�Vj�4����������9����P�>���Xnt0C�&Jz�G��Mi�[!W����6W&��0�*��ZK���q��q�L��qY�+H���0�*_�~��GO�Y�2�q���b~�����W��\��A���:Q�ϟ���I��>>qR�� �Q���)ב�z�~��FF���sw]�����j�xJ�Z���W�g�V�����߱1���y����m�O�]�5e�s�������CZ��=�5k8y��X�)oJ��=q��GnL��2��~*I�@��@�χ�:VY�/#��<l.~g�n����8�%-������t>�1Q�x�Ϙ�y��ݤі2��;ZwR���z��^��O�CL��P����B�C�����]��֧�^�v6+����A��y/�H�i�ݧ�o�&��5��S�_b~R�f�95��԰�i)Ose�L�O�+�ס9�Ct�,K�I���FsH� y�{{���bTQM�<.%CR:,sw��af��3xNO�9]>��u�|_Opϻ�sE\y�jD����#�P0$g���r�
#R��q5Z��յ�լ	��q���V�i�#rA5\|\�v$}\"��L�$�;$�P���Qx��c�s�����xX6�`q����s;�������/c��=(m*�<�uv�U'�{|^�t�GO�+�b>���Hg�-P��g��Ǩ�!��	�3�q>������x?M'iSx���)�e�އ�6#���	������	\���v�D��'���%��_ë��~�8���>��r�<�>���F �����Tȃ��2�!_�/9��Nڝ��CNԪ��*j���e�������[����Oa��\Y�I��ئ�j�zO����$�:M�X�x��$x'!J��i��	��%#������4�
m�H�*�ic������:�Kdg��~F����б�Jr�����cN��ǹ*y������PK   }y�X;n4GR  %  %   org/apache/maven/wrapper/Logger.class�S�NA��-l�]n�� r�Ee���ŀ!�Ú6�3�aYlw���Q|0Qb��C�nT(�Ϝ9�������/ �WATALCzw�>7jܵ���]Q�z������l)�8
TIhS�����a���p��!ӳm!�������6�ǀ�AC���>7}Q'
o�Sf�q<�t\����\#��Zqu����漫�
�
F5�a�a��#R��m�|����)j�*��vsٮ܁�	��쨐2�)��J��Fq���%G�4C�ҁ��r
�q�e�
�b�C{D*��kX��m_��]dg/�Td��আ[��0tV��y5�]�jp��n��&C��Q0��+�{�-!��Etӫ�Z�K'������4���,B�Jwۻ��Dc�7��B����y�S�7NH����j������ɪXu�t�E� �.-l�V�a<XY���N�N�yYF�g�;�!]"B�Fq�N�U@P��&p��M�uRw�F�Ч�~"�.�h}C��EC�~���O"���Q<�#���Z�&�'�/������C���a3�f;`�6lOO{�3T��c���P�ٜ�<F�;�PK   }y�Xb`3�N  ,  /   org/apache/maven/wrapper/MavenWrapperMain.class�Y	xT��o�&�	F�d�	�"	H��	d3��5>&/��̼a�M�V[�.jkKWk���E�I0E즭ݭ����k�ͶT���&��d&�������{�9�Y���=�"�ω�*�S����q�6��Gk����@~} 0w�W�;�@���B�Ft�V�h�1�6������Q-ѣ�r4`:�@X���ګEJ�+ڭ���v$�k�5s�N�_�b	��&����f@�	x�3��*ʥ�D�R��a6���LE)�,N��ף��A�ΛQ�:�ό£u
������Nq+���j�e��֩݉c���k�%)<,P]>��'v��"֩X/5v��=��FlR�YE9*�fuB�1:���y��A-3mM�V�JA����A��h��CE����3����`��e�-*�b���-�m�,-Q#������ۡE(p;.Vp����.ʴ+m������<m��:��%P��+D�f�W^�=
T4�I�<������qӠ�W�сt�P���, >W��W`]V��p�ԂA=��r�KV�f}D��f�p8hhD�m�Ih<|؉��]`s�d�N�����j�����c!\`$ ��Cf�+6e](����A݊^��^�x�,���m5B���*�r�/�_����f�=x]v�M`���e�����C1��}�5屮D��5*�p-��n9�h	d����$�� O&,�4d]�j�#�����p{ �7�q�>�&}�/)+em�L\8,��aLE 
�Fx$0�Rֶ�Q����!,�>4�����]��q��|���,V
��8�"+�	x3�%[�S��ɣ�*�����9������I�$vg�8�x��x)�%�&�th!��
^�����t~���T��q��<{K/�7�����\��D� k塂�|��������@�U*^-�F��cZT��z��;%��T��;��.��xk�U5��^O�#9qoT�&o�[�VK�jT�5�������B瓤.oSq�����}�A���P�N�»�̮m
j��m1�ʤ�	�f#du`�f�v�����hm:��^�O��U| X�0��4úψG�D��y[��R���aQ�Q|�e2��vï�VgV�lu������>�O*8��>�O��N22M���m޲6F§V��|Gæv�{į[YA����Yk���f �'�xS����MP��zǢ�a�5��ǁiVR�H'N�a�U<"��T5�l4��|�gU|�'��Cq-Kk��\qe�c
���Kx|~إ�d��������mO��s�+���p�0��:�u�)�헐�0v�l[܉`t�Kw��Ys��ZLwo��\#U�����T��!��W>��xޟ�<󹤉��&�p�I|[�wT<����Vʥe/9����c�����6T�u�$��Q���j�|8���Q��(b��$�{�?ď�P�atJ�6�x
]c�\�=;R��0;V����1[�Y�6�Q-��l�ρ�
t�(����G�xx��|O�;W>~��W�5$��Y|��+��,���}m��S�;���D�>ʦ��9����4�	��XD�ˮk��_$o�Q�W��y��ŪØ�_�Q��w��MbG��[�<	&'��?�Kſ�}�$����A#�+����$���ee�i�H�Y� K�"rRev�D�1��xp�=7�g:a_:�A�� ��u:Km���D�m�8{��1���sAU�t4�{;��|ޞ�֮/g�bU,���'.��xb�*\��w�foKC_{�P�2^�2�O
�۟�ut�~�LKb�nR}�X�R�J twuu�tu{{zۼ�d�LIg��e�6��`\V�4�g$����<}���oϨ��C�ޞ�.��!�mQ$kgCI�fH�>�@WO�Cl�Y����b�s�M�VY6i��u�C�h��<�L��Z4 �	b�9 �+�������[�U�K36���׬}I�i6�v.����J3���SN�Z{���,�L�GL;��Zrƾiط�����s�qߥ�������A�|�ѰfZW����kI��4��)�L�V��N��ΟCL�d�8���	T�i!գ��yqY�r2�ޕ����v�xt�m���e����q��\)V���|�r�4�L9�.kf����ڔq!����K��xC�x#ǛRƛᄐ���RNJ-G��X䙄��b��3�"���� *E��ZԐ��E
�����^9�L����)��3o+'�ҵj���1���a
����y�p�.���Ϊ)�ޙ_��F��4Z]�ey��;�
�F��z&�7��i\I�U�zVՋQ�g=����b�A	�AO׌h�l+ц!t��;�n�h����V\�w�
�B�W�-�ʡ4%�ʥ�kQJK�I�b��F+6b��Hlg��Z/�%��Vl��{r.� ���Qq�4��騬��(�]7PY噀1��#8|"y�R
|�I/�чe��JXJ�m�	�X+.;�t1��:QO	�,9��J=�b;z���7�����I�,S��n �3�p_�"���j��*Z�l�P
06Z�7+�
�fu	T-���+f�'�v E�R��RВ��&iTJz�rK�k]wO�'�֜yGR�%J�%��D�y]�	�}���	||�q�9VNb��!�����=X%����/�B�`��7��N⛧i�o���Y@��D���"DÅ�!�yb�:DE�&�	�=8�����L�ᨄ� )y5*l�h"��=
8#�[��z
.�r�Lb��G�k\����>�c(��3���L�g������y{%��J�o&�ێ*��𧁪NϣPH�G��v~���(���B��}��%"�3)��"{��F�T���WA��;7%"�'���˩�X���73*o��J9�q�\y=�ކ�n��]��͌/�Q��Y�\��^�B��4��	��#�J<�6(خ�����#M>Μ�r8�5^t���Jfl�+ma�b�D8��S�Y"
�D��,�_G�'��M��$��ú,���Dr'7��㻓Tx�^������^�労�hMf�K-L�%b9�NlR"ʬ�|@ˀ�~����+���
���PK   }y�X���|�    >   org/apache/maven/wrapper/PathAssembler$LocalDistribution.class�R]kA=w��vۤ��֪� %I����R������mv3M�lv���-��?�?�xg�m�A|�g��ޙ�����+�xpPrQ�QA��8"�E2
އ�2�	�#����Pj�5,������:գ@LE4��D\�$���t*u�A��Y&'a,��~����\�p��4!�CN{J��V4Qip�bi��Fh�Gݨ��/jJ�o�a���M��P�D��&�ԟ�Ch�yB+�/�r>V�M�w:��#����6Z�		W�Z'��{ә��<iޒzi��]�Hݍ㙋�����voX<�v�8h�[�iޔ}	ğ��v���惰�w~�:��p;�X���Z��`����#�s����iO62��x��	؛�J��o7ͫ�m�9��hH�,$��%�
���}�m��-�	���PK   }y�X\�@j#  �  ,   org/apache/maven/wrapper/PathAssembler.class�VYW�F�$�X�$��iK��p�f�l�%8eR��T�+ؒ#�@��������+=�š9m��W�zzzGR��vO�iF�{���e�̟���;�����
xx%T���ᒼ GR�6�_Rf,�����Z�<���j���P��E7�"rF�I*����h�EC�d#2*[�n�T��b0��'L���
ÖAې��Y5���]>ԡ^@��M�3��?�=�6��e�Ru��~N�zU�2�x�Yi��v+21%"�H؊ C���MeX�v�K��v$b����wIh�6�e(i}A�[�ʹm��5ʀl�p�%��c�ap�������6K�;6o���,Ct�����sCE\��-6�ЊP�a.��T�A�tH�`��X�A0SO-�P�FYF�$<�C�}:�f�[|���������9�ÜC��N�÷W��9U�Gp��c�dK:0�3pB�I^���ͤ�S�6�z�'�_�iP���Lھ��U"{n;�g�A	Cf�
7��$~=zB�;EԇQ�0&!�q�Mk&�����Pc�ʒc�ak0Z�EL2���)�'��lZQ-�,��r��\�	�(C������@0Z�
��1	2/�[韑��U�{E?Ł�T�>+a�ˊ�� �P�)uO�OO��ƦF��8�y	)���*��r�����]��E�dytl�L_ϸ����fّPLX�x�)L��R��QC�R��Xbض&3��,5M�jF��b�8C�� �ʪ��B���#�)���Eg3_xF³x��x�k	�˷��\HJ�İY5��ڼ�/jsևE����+e2HYl�tl|,:|�B�<�0�
MK֬I9���s����:�Y?�j�p6W�q�6,��;vR6T��.z��J�
����v=h,݇ȑ��@)���o�ၘY,F��8�^<,�	w���'�=�&I����m�╱�r��S��5[ٓЩ�0j�1K���3n�m�	#E�q�4���1���F�Y�o���4bMm�T(G���@��H��C���x��W���F��F]dh.�݀��&}Ehd4V�r`?٢oѻ�^܉��-9x��ȕ�s��I��q�P�kuD	t�zтj춑����g56:y��.�_�!�x1^Am��Bm94���9������尓�ݫW��k����^����?m�8D��n��TSeSU]�y�D�C����Hgf3]C8@+щCt���yg鋻1�̻q���_�q|H1� �a|D3���H"��R7�����'�T�$]�\����˃j�?>����)O{�z��� 'k'l6!G+�&`[f��� �Z\��T�3ֵw�F�$R��70�f�I~OA��<��O�I�C)I�<ف�b�m�)�پ�?�^��s��i�ހ���h0��f.P���u�����[�I�Θ
~�u��q�/@��=)��*����ћ�o
-~�zC�?�*œK����|�����������܀��[��ũ��2���'W�4�<��^+x1�������.
Ք�:�Q]İ�z�z��=q��J�������aT�"�G��PK   }y�XR(��  c  6   org/apache/maven/wrapper/SystemPropertiesHandler.class�ViWW~.$�Q4�-�$,q��*�J�Ģ�vH��H2'���n�M�jw>�� ��S{�������ԾwBHb��_��y���˽����g [0�B*%�d��`XzB9�������a��ѣj������3*�ɰ�J�jܷ_I�����RͰ��i��;�ƹ/�j�IS�5j�DB���X�P_,��>)�55̣�bæ�@�ܐb��G�s���Pϰ<ᐡ'�a�<�B���$��$��A����*�%��גi3d\I0�[T0yc~O�E�;~F��5���ו��VV�3�B3�J�GF��N}){8����u9+}#1C�T��܅V�+��W�DB�d��/p\��yn/t7�w:̭CI�dX�WNk���|Ê<��T��޴�pÅ.T:�����0t�y2Ƶf��4'2�,��)���؊mT�J�p��;#���\��/L�&�E3[����у�NS�2���JUH������-�m0��WF/ \3���~[�sa�/���؇%�я�(�E\�UMn(�n04�����@ �21�PW*� ŔT��6�>?,4�e�0BE�Y���(a4W'�=��}�(Uxc�8��t#�S�J8�}	��|��H��M�L��w�~E��9T�32�7dp���Q~Zt @��c2��pQn�Sb�Qf����q<W���t��bRl�vsa{c��'�\s����@V��ĠJ8!cB�[�f g�v\�"B:!C�N�5�t�����la��!!%�D�h8����ZԌYI�(L�8-Pp
�J<ͫ���𔌧�LQ�P�H�	Î2�q�n�'9�ŕZ��Ѯ�.�G�����!�Ik�ߡuˤ����#�VK�Ӊqn��Ρ��a%>����'�̘J7Ȧ���4�TO`��eXWv�FL�E�i�Q�Pv�30^�ˏS�UN�)q�l�|���wї��h�]^���rSVԖ�ʕ,�������/E��J���/^�b�l��Z��sǙ:Oz5!S	O�����aWtz����3mP���EG��	���K���6�<����T_��� �k7z�
�˄�݂ki�V�;�n\1FiWI{z���e����h�{g�.Ӧ���a[�*}� ^��V��IR�\Q�ڀ?� �͠*��	������~iuD\> �W�"�љ���_�B���h��O}�Gmn{�ݶk�hΡcl]daC��6{3؞Aw�v]��`�,����8�p��<��vۮ��6�G2x<�,���Y2m�e�v�$ϣ�����,�YLU���͋���h�6z��`7]�����0�t`O��m��y�@��t�V��H���7���.�бg�|���Ľ���\����0Y[O��"�J�j��x�v���=J����>��vRTP4>>�G�:�>�'��5��O	�
�#z#�Y��H>+�9I}A� \7� ቛ�J�I8O[	u�1	�7)��B*�,��OT��m��*	��1�y��*��������**��g���p�U��[G*�X���;Z]D=H����PK   }y�X��   a  '   org/apache/maven/wrapper/Verifier.class�P�N�@}.%W
�&F6�$,�*�UAY�#rӫ�%:�~���.:�0X����������a��6�I��L�N?yS��u��N,{�:�=3�ѹq���8�N�O�ƽ������?$l��8x�.�u^��`\HL>��J���%D���0���:[��Yߘ0����� !4/�S.�o�M�Β��V\�}ʗb(���|��F����UD��T54@�&���.�%���mPK   }y�X�W!  �
  3   org/apache/maven/wrapper/WrapperConfiguration.class���V�F��!��U �s	9PJ�ӠNM	�����>P�&� ��eɰ�3���*+Y�����G�-���G{f��g������ X��at�N�
���0�_p���5{�V?t�_��3܉͖B�axj�NT~�Ou��_�zY���zM���M�<6N�5��ك0�ĎVJf*{�Z.�ܭh�=m?_)fr���0��OA?Z�W�������L,ݴ���du��TQ�0�`#���Uչɠ��7�aY�r/BS0�O)�W/��]4i߿0�r�1LJ����e�Y-!��+x �����uiV-~�#�N[!s�9�l[?;��|���U*��vv')r�`�I�#�&惺�{��:C��~z1�xO|��Sm��L��U*�0�Hni�t��H����b!��TrZa�a�+a�zlTu�yU`�
�Z�ĪX|�`A0�~5���[R�,�܍�W
��kҽ9	������r˽P�&27{�O����|��'��m�d��p�Z�6i:%^��\����:7�#Z�O�������Z��Z�5C��d�sj�j�]�U�ɰ���p�Z��m�	�t�ʳ��wsӐ���ݚD�x��V���V�H��GT��zl(&�{�l˜�7L�Z�?�H����n'��Gc2o�>L��^��a7ފ��_� RS/��m��y�^;Է�㲣��xD-�A�o�ޕu<�7du���o��	����~?�G��H��隹�o/���kG(����uE�ƕ�;�x��=>y��5F�щ���xt���?�Ɯk�{��n���O �u�!0�{�3��x]��۲I��W
	$id��w>�J�X�����I������F�6R~�:y���)��.Q̲�a���3�/��(��d4�\
�R2Y���6 �>H;��� �$��-�'�X	�$)f���%Q8�W�� HJ
���d:�C1�6 3n�3P���A^IAJؓ��A�Sj"n�(��T�� Ⱦd_��|�5żi��+R�*��R��,At�9n��+�T�� �!y-Wd5rF1f�5_�7nL�PK   }y�X��e��    .   org/apache/maven/wrapper/WrapperExecutor.class�X`��~[���e�8�0D��BH�;�DX�����g�,	�d'@�-�{7-]��J-��(�@K-�t����~��$K� ���o�����#�}�^ ��U
��x��2F��-Ƥ�h�J���n��w�eX	>��a�{;;��#Y;��c��ɥ[�Q�7Z�J'��-33~�j�]!x�1i�$�d˘7[z{���63���I�QgC�H���촕om�ΥiհP�(g!k[�ނ� `��%*��h%,���p�` �8J��Õ�F+�km԰Lay)A�J��I�V2�
�a��cp�B]龭|djI�c��������]��	hmة�x��g]ӗM�քٱo�t��bN�p���8Ia�!���g}}==���|�Տ5
M^�C����5'eքF�҂�)&��А��hӳ�Rs� �&�:֢�d�$�1�LO����\5�~W�\D>�wpN���S���>|�5�ΰ�P�oI&Ƭ�l�p��k�5g�8gQ�gY�:�F��d����T�zXai唓���h�f����%�����G��]G�d�<'J���tl�|_O��5R��ulG�*��f�H��I[����@�b ���3햁��V�G���*,ʘv1�p��A?��e�h�f#c
�~d���r�u��j\�c;��3f���~X)e��X2�.3W?���W��c7F������pE�$4u���~;�*�X.ձ�9�!t̒j�Qh�`�	��tBG����J/�JĴ�~�����| mY�#@�vZ����}��@�0���E�y^���@�>?�*�=z���5��� ���N��N��Z��u\#r���V��:jaħ�����vo-ހ7jx��7�-��Y?nN&�������u�a�9�t�`���:ށw�9k(ch+"�N���:ރ���V�!:�S��>���i{r*!�+d7����-)��W5|(�8��¡��G�[����޲94U���	��
�~T��&��#�U��tr��M���8n��	��-�ȿ?c��$��LL�k8�_^��q� Ԃ�i�:QM��܎;4ܩ�.I@�sx�RN��<aJ�}V�ݒe2�K�K�B=�cFH�{c)s��F�����8$�klJ����r��w�u���EE��t|A��:�r�}Q���+�1�hb����!VW����V|�L� ���H;=4b���bI!:�J'�=4i�-�xH����k�:�K8��-͑=��m��G�-���T��=Ԣ��q�8�cI?���Z��'t|O��O2;,)�~���5���q�z�S~���Ñ���x-��pgg��ߎ_����Is�'H�k�V�����:~�ߋ��澞�Ô���E�?Ɏ�r#����:��i?b�~���-9��k�w��CR�ƸJ�G�O�����tB�9Տ����H"=-��-�7���a`g[�gu<��	s�3���T�4X[[_T;9�,�\)�C�(��*UM�*�O��N��Q�4���/mv]�]���x2cj*P�N��Z��j�b#���eS�D2�Hp�Y��j�T碲�7���Q����R�~���fsg"c�$t`���N��~�m]�V�PGkj���Q����YaS{#%��M��j2�%�[�l�|Dˍ�B��
�j����K��.+�!U�S�ں�đx�M�\C{��n��q!���t�tF��kV�w���:7�w�t��{�zz;���
��b�^��f%�A�̋L	��m��"�vv���{�:�WJ�z���U&+[Y^B��6̞`86��j��h��������SUof���m�vf.�,t�\>ڭ4��4q������O�M���$G�E���ى�f�_r\<(��A�I�4x:� ��1�-�,`���2R��o}�Bo �{,6��/E�j&s��Yr�;b21��|#�33�lz�B�^hAAtFS�֬,^��'��+��"��H}E���b��)�]L\(�S�S�/����ab:f��b)�0�aO��g%3(Fz�A�� �9��
�_�xޤvWĘ#\_+*�#�ٵ-9aJ��J��G�1#��vͤ�ĜL�e��V��*�~a �B�V'A��9��S�)_�q�h|�E�K8n,��8R4��㦢������"(Ws~���y��{j�Z��pԆ�e�P=��9���₡i,�F��L���<g�&��O�%Q�9�k���4;MY;���,���Xy�݅P�d�=U7�f�8�S�s$���vD��s}؎cx�?��=͵G�.��N�k	�]�� ��p��'�w�e�Mw��1r76E\Ŷ�c��lm�,W�љC���@^9Ħ�#�]��%����9�#2��&������C3���w箘C�Jw������׹�k���9��S3x[�rg�ξ�f۝��]Fğ�Ӹ�;��x?�=O^�e|�O�l�"l�Q�����4��W��}��g�{Џ+1��� ���؁[q!�g;�.&��w�O�����Fr�!�o�z�A׫3ԙ�O:�RΗ�b����z���5��ט�J�W��^�I�*�k豏���nG�RwCAX�:Kҟ��id����i�f�[��>�0Vұ��3�s�9�~�h~>�{����M9��>\}2}~9�G8�Hߘ�7������X�T����Fs�qy���9��~��W��݄`���z_����7�<���������!�>�-��fwaO��-�n��9Fk�Y�Rz`/��q��ŀ&��'�)ZC���Y�L�k
WDn�`:\OncL�a������V����M�lΉg<���a�M��pj��j�?��"�b�j��̂s���6�ߖΐ9��������1�,�WU�BWp������� �K`O�����4��߾�����A�{�Y�������
�B-R�Z���ķ���k�z-ׯ��נ�;��Z����v�)�x�*_�׮SZ�{�d^Rk������b��'�Y�i�M��/�/���|q�߁���I�(r�ߋ�R�m}�G_#���7��;���_�T�lr�麈�n��N�*�0��T"U��j��:R��Ku;^��K� վ����@����g@+��~�1��{�&|�����@M�y�?D�rM������U������ȴ:�����$������B���k$.���D�W�)� �~)'�<��A�ꮒ0:J�s^Ty�]�N��`{�'P�?�̼���PA����R����PK   }y�X��   T  ?   org/apache/maven/wrapper/cli/AbstractCommandLineConverter.class�UaO�P=�ut�!���QPa�HP��$,K�fW�(�ڥ�@�����D�?�x_[�����@���}�{�}���� �c[BB"�(��g9��4��Y�c���]���i��6CH��DD�m��jeM?1ԒvfX깣�ˆ��ESݵK%�:Θ�q�9Ñ "$aQCQ��0�fG��|�u4�m�ڵ�3�q�a@��/f��kr�71�x���(0��<˰'g�;�'-_4R����qE�w�0�qL0��/���"[q��s�E1����q�oU\��I��|K�4W2Ð뙿���Y��n�o8@a�>&��<}P-�'��f�غV�i��߃����T�:�L�a�&�E�����Mg��mo��è�)P/c�6�M�Z�u�ID�tC��uL����Ս�k�VE��]w��l�)TK�����]<2��zw1ٵ�89wg��Sɰ��M��QWSN��ҩ.�6���^���}Maُ��٭V���T�C�2�ґ]utc��?�i��9�0G�<����a��i����Pd<�\�}�E������xJϨ_�gxNq�Ff�"]Fȴ��]PQ��	��>(�$%�T�Hc�u�{^�"��f<�Yk@�Wq(�0���8�8E�j�^���eZU�Ҫ/<a��C�|��,)5�j��ljv���Ku�����mKs3ߺǷ�}�(P�F(InD�����;�@].���B���U�'��%^[#�'!O��\��I,�įM��j7�z(�oџ�J!��7PK   }y�Xm�v��  -  I   org/apache/maven/wrapper/cli/AbstractPropertiesCommandLineConverter.class�V[WU�N3a�0���Jm��D���Z����p�� m����83����껯��Z]�����A��}&�-��M�b�9{����߾����0��%��O� Ï&��Y�+e%���Kʦ�ǷL�\V�x��ŧV,�Tr��Q*)z>��괡o����M���ٷ|�HVD��}]�T�EE/�3���	ZD�2Zq�a�D�L�D��Z��v��=;�e[3t��H��x�[o���Mw{ͨ��ռ��pADHF'���x тbZ��!~��I�T_���+"zd\ī���˄��XSf�RRu�b����d�r�.6��ʙ�3�P_�qWΓ��f��"�)ގ����wkmV)K�OB"�<n��r��?`����z��16���!oa�a�,J���R����n>;n�5�N�P����q׉�ïU�b����N�\9i� Q:)�&�l��-"�qNݶ��~�O�#c
������u5g'�a`7%��#�]�%E��W��WB��Np�-HcVĜ�y,0��C�0�r�L^{-z%`q<���>�kUx!㜟T�a����9��;$,�x��-����-[�s�M�('��ޏe,�"#Wk_�S�X�Nm�
̴�'m|�\�������"I�i#�����+�5�,y4���b2�L��j��I�G����<�,�ϲ�H�N{�4G�J%���+��Z}�&�!�Jn%xbD"K��S����Z�S�A�)J�ڇ�:�7��^$x��s��p�wʻ�;���[�m�	W���Ω5�"v��8���-�;���?��	�8kl�g�wL3��Ѭj�y:��T*��y�2lT|�gr�;(e���S�h<�ѓ�� �%\�?�f0~���cZ�i�n��3��ir
=�aV�)�6 �< �B����Gڣb�UZ��>����!.��T�Km�*���*^����R���!��
�p��P}��w�ִ�8���V��
��i�wv��a9��O�[z�7���*r����s�ٳA2��d���͡	X�r�U��x�9F�h����4y���	�[NW����O��e�9��a�[����W�~������}��1�Np�]���>�GA�F�N2E�%���ԣ�iQQ�Y�uk�{�,��:�-ÄE~�{�rI�$�4A�"&��W ����?�Ֆ�a�%��"��'_�������>E6?w��GR��^ ]ţ�4H�G�����KY������K|�O�PK   }y�X�:�dP  g  ?   org/apache/maven/wrapper/cli/CommandLineArgumentException.class��OKAƟI�M�,K�(�[i4�.a!��!�������2��}�NB�>@*�Y���C30�����3������%l g#_F���2�3���T&"��.�J�ZH�t	j�η��(!���Р6�H�Z�X�&*����u��-��Py�E̝p�)�t�XqE]_�^L�!���Ҁ�de�|/s��I?�����AT��e��)a�e0�LDL�q�ݱC`<�����uO����X'kk�LPꇩr��05��ua h¼�Y ��Y�^W�&n��s��,��υ�X+O����B�
v3��=T5ð�K�=rzv�}>����E��XȾ`�f�}d#ֲ��'PK   }y�Xlk�I  �  7   org/apache/maven/wrapper/cli/CommandLineConverter.class�R�J�0>�������7�.4�
���L��в��;֌6-YW}6/| J�R���z��|�~Nr>��? �-(8KdHYʂ�1�Q�W��%"N�I31p��n"r�J�C�31�������m�~P֏�i��9���[�i�1��F�z�872�(�)�@��`�X"�cS��L��$��q���r�����8]�Ly~���g�OL�p0ӯ��rym�[���U\--<�����g��;�GfF@+J��Ss��[�:ƹ��G���4�'V� ��|5��B	,u*��*�ծkY7 �4 [i�m������=����7PK   }y�X��I�U  �  4   org/apache/maven/wrapper/cli/CommandLineOption.class�VIsG�ڒ=�4^P�؃	K�`dC����,�����IFR#��(�K�?�%��KI*6E�J�r�?����T*���-���p����{�����?���W ��u=�)��E���XK�YL.�6y�a��Mݙf�Ec�
�\���ɛZe#˝ ����`@� �NYv1����O����L>��r��ɼ�'�V�����n�Ų�[&�b��
�P���S�؅���T�`7�`�䲥�K����L�"mh�JJPڣb��R5�X-q�YzV�!��U�����m-gp��.��e3D�1�9)O�0�#
>Pq�vP�T(�����:n�ք�	"J�MW<����zJA��Y���b'1���$���D�%cw�.����N�\��^qDvΩ��y�`����jB�dZ=������Vu��� .�ʟR1%�B^�ۺ[Oq9��2�P/�Ƣ�.bm"	�񪊴�x��yM@�h�Ij��5�q�<�,:n���Ŝp�1��h��� 2���;м0:6�[�o���O(I����ȵY��� ����7���ᲊܣ�q�����p��z�Ԝ�M�v�������b��ç�B�Ǜ͝NŖ���V����9[��r�^�5w8c�5cY�uq��>K�����i�3�6�-�Lh����J���w	~ �h�G�ZY�&����D�ȝŝfW�<�'��x����|Jΐ�;'I�@������pI+W�,�h�㼼�c�8H>gCJ��y�jPD��N	Y�&�0��������p����=��Ɛ�[�h�gR�ZU;ϯ�Α�HN	�8B��C_�>���t���ы_?ߧ?z����\�r���u�t{�)����;|��z9�,���K�o!�	�[�$^a���W8��hs��7��?#8�
���x�׏���}���@�N���aD�8H�s>�1|F���t������sh��ny"X��JkM�i������`�R�_!�M?�ȋ��]ƕ��0�<p���q�E�W���l��� �I"t�p��o��b�#�	V@b1�:a��q�0&;b��'1���mf�C0���2iGJ<���m���Qڅ����P����:�?%��)3�h�ԄX�qi�֙7�&�1�����m�\I�o�;�4�n��Rw�9���f]�m�T}.�Ij�%�&5�u�-j�N`�Zj�
q���g���%��T�j�UOh\��P*pdS�?��`��#���j��M�en�2��K�d���&�`����p}��~��� k��y�9,����" ��!g%䐀����C�P3​��s�3mo�ͣ.�e�w�?!�)q���,+{�)��}ֱ�gZ����_�Z_�PK   }y�X�#�ر     6   org/apache/maven/wrapper/cli/CommandLineParser$1.class��A��@D�k4�f5�Y��V�†(x�?�OL�t�nu�6��V����^�
�r�?��#E�0q�Pܰދ��$V�yn�J�R�]]��W��-� �{�"!d�X����V����������e8�/���ƅ�k9�]��O���0\Z+~n8	�m<�F'��.��	z��G(:*�� PK   }y�X�@Ƀ  �  I   org/apache/maven/wrapper/cli/CommandLineParser$AfterFirstSubCommand.class�VmSW~N,,�	T���=
$��[���T	��>^�mXMv3�E�#����ؖ�N�u�����I�XR��v��ᾜ��ܳ���~��g g�$-bHP
����u톺���~��M7���g��FC�;e�w7U�r�L���W��U���l��e���e�3S�g��z���N�Va��3�0���]����q���4c�k1!�4#/��*�R���\)��[��[��xF�r���\�3�blg�w�=e�E^�^W��(N`���N���+\�@͍gf:Zu�����׉��%x7FBx�'��o��~��w,�`�O��#���R�o��p��8�Ot�g�{ӯF���b�c�s��d[w��o�L��]닪�A�H����4�˜`��1�ퟱ�Ә�^D���������rG�_<�d)��o�Ҍ��ƶ~���L�G�o����.1�oW�[\_��DA�^w��`�B��R���4�*�: 
vߨ�f�v����
TaM�}�:�J�g�����4ݪ`�����������%�V��y=,�aI��
�X5�w�ӉN�Y�p�U�w�R]�Z.��x�g�d�V+8�W�������+��W��y�qA`��/\��{��\����"=DC���{O�i�N�j�>�8����~����}#��{���OP8�y��A��QY�"i��QK�h�؍I;��Yt�?�9�稝<�0����'0~���q<�_0�1�+&pj+G��Yy�����;��Vn�����̹G��udڠT0.�8!��ī��G/�+�.2����Ʃ	ڹL�0�2�� >�\!W�� "/Ҧ�F�+�+�Xګ/ڐ��:֐_m�(�K�W�6�W�5�aq^I&�I�:�x )�L�X�&��ly)�Sn���η��PK   }y�X�wM��  0  A   org/apache/maven/wrapper/cli/CommandLineParser$AfterOptions.class��kOA�߳-ݲ-�� �Ze[/T��JL*��5�ڵ,��d�\�O~�x!���2�ٮe� Yi����s�y��ə����O �1�AADE4��۩�bU���.�K�p����嚩O����*E�2��0���]�y�ꚶ� ���#L��	�+\a��!�N�T� L�T�l�]���C�owϳ�N�EW�H�)�\���!̄���Z�����~4�qL��8��HqY��&��>�:�U-�66)�}����ㄊ�$Nb�0N.�KT*�7]G�Dm��}��`�D�N�~��^�k�E�y!kl�-�eQ+	ǔc�u�L�ٻawL�!]��6��[ D�S�b�^�mkbi�cn���ޖZ�t���L�֬m�U0��&��9#tU��l#(�9����ڼ�攍'�̒��9c2,��S�2��h4����ay��a���DK�$W���	�����.��6r\6�|�pj��ɽ��uS<���m"C�P3ٯHn�P�i~v#�7��-ΰ��9gq�zK��yz�[9K�|l�䂠w��b(���c�|�3��Ց����l��Ra�=@�5���)({���p�
F|�CϚ�f��/�C�W.hqZ	H�Zd�IN�8���G򚖊2�m����
c-N/l���UO�5�s:��r� �_Q��������&����PK   }y�X1�GX6  �  J   org/apache/maven/wrapper/cli/CommandLineParser$BeforeFirstSubCommand.class�VmWE~nXXM1`Q��@$���{��i�P߆e�&���R�G�࿨G=T9j����?C�wv�J�l��fg�ܹw�s������� Ɛ#���Z�l'�{�ܕ��x(�ā#����0�ļ](kg%g����='�َ\�9E7��]�'�M欜;M0�V�YL���)픭J�o6p�f��n�؝$$���P@4b����lk�@8%I���q����T�urg�UZZ�����H䅕M�ө
��ކ�0�h��f�M\"3AӔ�b��$�>8��~�n.�X{�F���u�c���N�j��%\-Gc}��4���E::�C���2���CW��mK�U�̀I�c=����N����#Fh4���#�O�0z0��5H����ʻ���⬎a��g��~AZ��#Sz��c�p��sg	��<�vڞBgoO�wH�(!�2�¸��:�7p�'�}��
�� �����{6sKZa!�4Ih�`EA0���U�XaLaF9�%�����o��/�W��I���>����~a[:��v�%-+�)��ɩqI��׊��W�W7�l\��Z� ]��&�p\A��^����Z�_��ɪ�OY�3b��c�e| L��͗9�(��A�������^�TǶУ�j�U3B	n�_����L&h�)�!�=�ӲeIg>/�Eɬj}E��V������%�%�^	�^�6��-NT��E=1j�|S�๙�=��S2$�u7�"�HD�ܫa	������C��߈�����;�0�m���1��54�u,����X�X��,����bG�#6�̭r���% �М��!"k��?�?�=���<��k�0싯|��#�d�<썱~���2-I��dj�O0v��ߎ0�Q+۵�<�bT��b��	�y
4�.�� M#E3X�YlЂј��I|�[�u�[�^���*��n���h�؋=�'�l�q9�q��R�묫�߇��!�S�i�s�U^8֑��׻큪zw��%�S��g���CqI���Dw�'+�Ϲ��B���s-�W<���ۚz��0Y������PK   }y�X�w5fr  e  T   org/apache/maven/wrapper/cli/CommandLineParser$CaseInsensitiveStringComparator.class�S[OA�f۲�le-E���lA�1%$��I�rIJx�Clg��-�W������g�1��n�P�41��9���?�|��s����v���N�;�s�Xx=~*��^�~_h��J��z\��R�=�C���<
�H��V���PX�k�abS*m1����&M�,�{�O������?�a
�������b2��1��haJ3��WCk��A��`��y�묃9�3؉�a���0m�f���Hv��Y��A����dv�C���a�,�f������c::�!�^���F��~$�n
�P�poPmyt���V��wۼ�twi���F-%�t�Et��;�����eq\R�����ʰ1~.��%;�G���?u~ea����"�\+h_��f�#��L��PJ�z���mxk�Ͱ�MڠU���}O��=�4���v�nIF2S��)�߸aeq�tg�{X$��+I�5E�I*���m-~Df����YS1R�DXS���<k:F-3T���R\���Yԃ��xD8������7T����+�H�˕o�Z�s������'���Z�s\��i��5I+�~ �À��3XA��)���2*�\ų��E�5���M�;�PK   }y�X���  o  K   org/apache/maven/wrapper/cli/CommandLineParser$KnownOptionParserState.class�X[x��ǖ��2	F�MlJ"Z���E���qc;�N�:В��؛Ȼ���N���{��@k��Ih!@�X�q��B(�Jo�ח���/���k{��J�%��_��ٳ�3��s�����^|@;���
e(�mZ�15��G�ب:��qKM�4+O걝��j$ztCۯZ�f��5�qc_��M���wTG(߮��)���)NeG���<�������7�
�]�y��7�)+��A����ݮkh-�A!H�R��Cv��-K�ׂhm)6Hu�h5
��<������'c]����m'�
T2�5��޲�%��ZZ���Lk�@Eϥ�<��G�J\�Hs^Pn^'X�:����yOq1^���Z>(�!�	��_�|EU�$bU7HlD�@��`�r��I�hD���a�٣�]�pzT37��l{��sw�k�� �n�G4'iH��cd�{c$ajv�0����"�Q}-
�(�n���1�7tD�?ڱE�V���߳/�۴FU���'�k�=�
;
G�Ќ���U�hi`7Kt�#d0��Έ�� �( ]FDM9�s����v$e�czBKDa$~����N�R�[�V�F1YHu��&y��+9����(�
�L���JjL����呤}
�I�g����0�+�4�sܲy���g}�n�fw)����"q��MA�L�zqp�#�	��+TA�+�vs����>s)$�N|R��$��a��e�0*9j<��vݦ�V��ۗ�,�K�!�$$4�}%v�4���,SN,�nŖ�+P� �?�9"�sNl���(�
F%��K�J�#��W��!�������yP�K��t��)ws��~g�;G);2�YZ�eLb����Y�cZ�@u�������J|����P⎨v�v��_��"�D�3�C`M}Ca�b�~E�\XWRԳ�c'�#q/��FNإ�,-�2�e�,^�0G�	|C�7%��oQ�<f�K�-��}��!�fl?�qZ����&쎴�LhV��2��h~}@�A��v6ePuo^l�)��v��IV�P����pٙA�X`m�$�I��'*5����)G%�����
�8�Q�\d�ċ�%����Y�*�kz�gg��
��8��nZ��DA4�>�����E}��͏R�_6T'm�����^h���v�	}��K�i�u(I=�3�&TK�w�3�z�_�0�B���^�1ԩ���������]P͉��P�HQ�h�������Z';i���{��vʭ{Y�o]Z����w��5J��r���6n6s������c�ʹ�nՙ�cZ�7Ān�Ь�Iնy���ܵ^�J|��`��?���XԴ},zD+��%�r���HR�ou��e�.U�.�x��<SQ�wnwf	��A�9z{�$M��)(Q1���9TN#<��h�j��S�.Z���њ�)lx����:(�x����J<�Z�֋S� N�Q��s�,e�����w�+1�RWb�Wb�e�"��&���@OQ��O V�͠�!\;���p,�6�F_@M�6Oc�+�l�~���3�U���Q���hk.���'��'��~��4nϠS<�n��jAn�}���%9����H~{}ć�O�sټ.��Ir0Y����X5����s��f?�T�Y��Ϲ����7<��K�o���<(�[;�]9����}E��]�|q/����.�w�A��9��3�����YU{�i�-�#�p8p*�T60�⁙EZ��i��l	�8������������������s������28��2�a�G��I��'��{?}a�|4�3��9~��� �WQ).�N�F̾Hxw�71.��I�6&���Έ?�u�g�CϿ����o�����f��I��уY����Nb�/�O�5(o|{}+��7jo\K ��T���pآ�M�5���i�g���kq�7.GM�W�W:�g�į�,������q�����U�Z\p-��߹��	�NU�Nz^D-��&�F��������=��vo��PK   }y�XmKs�  �  J   org/apache/maven/wrapper/cli/CommandLineParser$MissingOptionArgState.class��[OQ��SJ�[( �xD�Y�"Z��\��	�eSִg����/���A��?��8g���a�4ٝ9��������O �&BGa��,����(ozM�RߵE�n�z�j��V�&�fɔƪ��}e:�)++u״�S���
� D��t���R@b�p<�lv=��Q$T�IB1 �F�\�$Wg�R����Y^��gg�M�ߣ�}������L��zUȊ���,�l�m��⢆K�Lxr�섄%9�^5�3��m�x+1\�p�)K����զ�6������a�^s��ܮ�%��S�,����&�֭��k��ZlTy��d�Eu]ئ��Ű�e:���9��0Ξ����1�;�]Q�O5JHZrْG�i�؏-�Mޛɮv�����"��&�c�0�g�fm�ec�TR��W��R�|U8��D1M��o�?&^O�ەl�(&�v�	�1��|�j���ռ2��s�B�N���\�;�}vC���.t�N�� A.Fym��[�x����χ}h$�?@�K�����Z8�&'�����y����rߠ�B~� �_O�>x��F`��q��b+�v�>��_`\����'�[j�7��f�y�����Q\㛿����}�pZ#��K�O�YPц`����X-�
6�	�,�;�&��S�����.�>y�0V���~� ��PK   }y�XV�{�  �  K   org/apache/maven/wrapper/cli/CommandLineParser$OptionAwareParserState.class�UmOA~��^9��"TŪ i�^)��Ҁ1$�i�߶׵��w���C��/�45���Q�٣`	rD���y晝�����O ��Ћz�5�A�a�v:osc]�-�),}���pt�i�%���V�lZb�;�p��ڞi[[��h*�C�[7�t�!_汨"�^��>hs:żi���ى�a�������Yq$���SNd���w9%V!�k8����ap���or�ɭ�^��j3/T$qA�E�0Đ��������U��ǠTU�pE�U�0|ʡ1��0��g��3AL4\g(ܗ���p�L��*��Y�Ҳ^��$�V����`(���n��*Ƒ�����&d㿧R��Kv�j4.+��p��Z�4�e���*wL)w�a�Wett�*2$���n�I�[��M�-��&h�����4dD��5�Z�����G����0F��2owR�V��K���V�$>��Ĳ�Sjr��)y\���P������%��]��a�Q�sB�H$�C����'�2ERY^H�Od'�������6b���7�{V��(T�F��!G�$�ht�$1B��qo��C��4Gf�(����s��G���r�w�]���Y����"rV!�1r5H���#���5)I�����:��'��Ĕ�XQ�w��q�;4V{�P.C��}�� Y?��a����*����R{L�!�����C PK   }y�X��`�  �  E   org/apache/maven/wrapper/cli/CommandLineParser$OptionComparator.class�TmOA~��zP

UPD����
"�VM�T0�!�oKٔ#�]swE�?�/~#��?�8{WAڪ��fvfw�gf����� ,�E
B*�1Dp�aӲ+����j�X��������UC�[�7��)�p���n�5,��unsײn���n0�R�(�����#~̵*7+����(�Q� ��7�>�3,�4��!Z��;�t��p��V��b���eSrmì\�E	���R��ά.iHⶊ;1�`�a�
jުV�?�P͠ĶR�N~���6���o�R�������>�N@�y��1\�X�33���=�a�j��	��T���nId�=��3�oN�_)=��tAE�a���u@��偝Fm_�o�~�,�E�̫{�6�i��m;hC�>�h�<[�!4���9BD���h�x�}���4��ݒ.P��u@�yM�¦T���\���]2*&w�3��	AK=ւR�A��%�a��+CV(�fAb�LS��*w��0~�{���ɗ�<>T<��%�a��z:]���$�N�ViZi$����Ϥ(���tCa�%=�o��I2� ��?�e�OgO=G�;��_���\�i5|�1��	����e�>�H!��1�D�����ͨR[��J-C��&�E��N�OHǷ	�"�$�e�н蝙�@�}��:�K���`K�u+�Y.���}���
I�I�LW��p	<B����3o�
V=�c��W�ulb�"�c��<�a��<FPK   }y�X7A�ϳ  !  F   org/apache/maven/wrapper/cli/CommandLineParser$OptionParserState.class�SMO�@���$i���~іC�Dm*�T�RH�r�1+��^[��?�kz��C ?��['Bpq�e�=͛��^������(�\���*��Ą�HEp*�X�K��0"M��H��$��>(-��ɤY?L�J�Za%��Iiew	�NwTG��? ��Ĺ�#�C�p|&K���	,8��I<��G�Y$��F�(WϚ�=U��྿�#4���ya�MH��t���t�'��&��|������Ck�{��o��~��N�8_˓i$]��;Ewl��#B�b��]|����	��.�}K��R 4�i-M?Y&�*^6��",޺�*��?S�����͢	xk�zjp_�� p&\���8���?��0���I�h0oLx�&��+@+�E,���s\v��S�g�K3s�ۼ=L�s�O�\��g��(3��^�_3�q�V�PK   }y�XO��4�    A   org/apache/maven/wrapper/cli/CommandLineParser$OptionString.class�T�NA�N/l[���\TT�v�l[�ZP#��A�b�߰ݔ�v��]P�'�/$�&>�E<��"���!��˙���sf��ɏ_ �4��
b*��!�����0�M�.�L[��F�tu�f��N�.�ʊe������Z÷{�w-�J�q�)!��#��^vUo.�S�(�"��[�l�L�ɶol��6RPѧ�_��[ֶvL�'D���%HT��	�|>�a�(��bc��-�)��ｷ�m�PQ�
&#�.<۵j�Ma}���b78B�4�B�w"k3�4eIqK��		�i�̶C�	�(/�S�IH��[^�&����I�-;�d�Vw�[��Vl�LY ���Zrc����V.pʄ�u__�FH��_E��i��W�sA6Eٔ��Y��0Lϛ*.��M�B�w(�ug�5��L�pü�ԗ�m��5�y��`�0ߝ�8�E�B�ۄ��J��!����2�m�>�6�� ��lS��h���f���At.�� �"F1$)�~�A��M��H��`$Ex��9t�s`Q��t����b�'�7�qUN����;��om-�>����h�)�C�@���u�W�~��kA�
�t��Ƹv���itM��3L�&S��b^c�1ɬ��C98�I ך[N���+�$D�p�t�p�MG�ߑ���PG� h�QP�e,a���t�PK   }y�XX����  �  K   org/apache/maven/wrapper/cli/CommandLineParser$OptionStringComparator.class�T�OA�����q����ł-E��SB$��&LjH�m)����k��O��/�hb�տ�g�� M�fvf�7��۟��|��u
"TQ�0<w����\V�	�z��FCxV�f[y�^��A�v�+���m���v*�mp���гa;v��I��t��}�!?�V�;kw�P��04�`���IH5�T�j�T�p�a������o���^�Ò��j��%x6'����8�V��l�x�M�Ӓ.�	�5L0���f`׬˽T���ʬ;����^��Y��n����g�}ۨU�gx�-��'�#Fn���|�_��hR~���co�n�+��n�7��]iK��:-�]�mT����E <b�|XdX�~F}%���)'�q[�Z���w�������-��;�-�CF�q���q����Bwe�m�����5��n��E���`H�Ӡ�N/����"�HF�Ρ}&EA���e��� L�>I&�������ʜAW?BS?A�l�_ޭ��c��	��i�D�ǈ��_a�a��P��)�D�ē�(�(�8��f�q������dX�J+i� �=�ð�-<"M�'g�xbNc�4��7�c<c�`��W7��N�NH:�!��|G�<�3�=�GOb�Ҵ�	/�)�5�n�S�J������[t��C��̇�O�J��a��,LP�,5#�Uy���PK   }y�X����	  2  @   org/apache/maven/wrapper/cli/CommandLineParser$ParserState.class�SMo�@}�8upS��P(P ih\ B�@B��JA9p�$��U���n��ąH��(Ĭ�V���Z�gf�͛7��?��5)�mX9d�Bx�+F�;��/Τr?k1I�v���|_�^�S�X�P�Ĵ"I�ʁ���!]��d�B�D�	w(T�=��n��*c�jUk�jc=�<
S�V�=�'ؾ�XKH�(7g��O����ux�w��(:C^)6�����L<^����d����B}�#9���(�E�zṛ�o���%�j*k���m.�6��ּ��@�>^4�K%P�w�GxY^��Y��&����/�KpZ������9���f�&�>(%uc(�P�6�j���qN4oc����gD(̭��#�/;k�/u����9r����O���I��6����� 6P,�b��l�:g�u���@��M)�)%��&�naĄ�q'&��]^'ܻPW�c~g�mM(��2o4n�_�M�ۚK.N��\���>�ic���D���([*l�l�x>�PK   }y�X�Ć��  g  M   org/apache/maven/wrapper/cli/CommandLineParser$UnknownOptionParserState.class�UISQ�:� !"�"j2,	���R�UR����L��d&5� W��)�<����^�xQ�~3�M�
<��t��M�~�~�y`w���B@EM���M��Hoꉼ���Ķ-
�N�sFb��煙Y6L}U�E�xb>3�m�Q�1,��[s�����p�	�c�OŖH䄙M�9�afg������T9;F�6��Hf�04��9��6V'��X��r��u��6����:k��J*:q�В>�!$eMBu���,!X�"�;��XΩ�E!l��\��u>?�\ᒊ˸Bi,OB��d��8�H�\���uJ.�"��K��R~C����.�������.o�M�HXj4����,�-�;Eq�Ζ��p���m��a;+��� ��=�Y���i�&(����?_ϓ\$�o')�Q9��a�0�83�f��~ߐ<Z�3"�!�K��ۋ9Q,��nW�58����"�����6�Ǫ��n��F�A�o�¯�����u���JnO,�X���y��U�e�Fi�{P��=D��=t��Wy��	���a����3���L� p�w&�;�.}�L�����]��Z����請�z�e\B}e�D�2����_�ߪ��c�e�i�������QO��Q�cmw}�Vb�g�"��v?u�Yt"������Ϫ�"jD���֙A��684| ��+������S�J�H�g��xV� $�r6�1��a�E��$�~wg
�8�i����Na��m	Q~PK   }y�X/���x  �)  4   org/apache/maven/wrapper/cli/CommandLineParser.class�Z{`�Օ��df���B � J�!IHHD@H h�jP�̗d`2g&@,j}�g�֢�>Z����"!�W�V���խ[���v��ڧu��<�w�<2�Bp�����=��s�=�wνɋ�
�l�/Y�6�0�K03n+�:��v����l˷���N;\��׆::��o�?h���;,��ٝa�Ŋ�C��a0�Vn�6[��P�Z�s�J-�6��`�����m	��T���S4��)��/�n�RLGbr���@y�թ%��$����[m_\��1�		MVuE;���d[1�rP�qN01^A��X�����H��d���C����� '�������?Z-��̴^U�U���i� U�5j~2���H��)��y��B5�����NK�p�i�#<8U1�Pt�{P�I�a��r8-!_˨	��n:�eEV�#\hZ������*Nq�"�R�nb6���*ˋR븴�����A�#ɗZU�8Z$a20�g�h.����B�X$�?�y��i���TkC� �f��y�Q��d`wӨݛ���j���N�ޝ)X:�����n����p$�����`9��jg�}�w����nS���hk.V��@��UXM���[Ŧ/ƥy�.>^��s�h�\�aMBJ��h�"��X~�S_6фuGP�M�N�f�'�6�+.4a�YP9L�c�ƨ�uyVw�͏p4Gđ����XU��� ��̃�h5цv��}Q��bId�:U�Ԯ��>�#�~T ����O@��^���߰��ԋL����+JA55х͂�HWs$�Eu�Cݦvj��n\̜��ܞ��?\+b�S��Jl�%.5q�&X�E
F�MQ]���E��3UC7�x�^_8�D9�*W��t�?賷�je���uj�Z���T:�ev�6�`�F|�BCA*��a�b�3������6�VU���}�T+B2�K��$>7��bk���f���h>�NM�|Y8b)܎)�&�0�SeZnK(����
���.��`K�t�>UD��7�b�R���1q/���&m
2��2:�Ҫ���&��%�t�J��M�Ha��6;�Җ�z�ďU�qK��΀��f���Cx�
�`[�]�c�ʛGM<��07l�{/5�c�W�ZNbw�}�����0�V7�Я`䉁�s��I��=�V���6+��g��+g�����ƳJ��n%���������ܐ��XD׳�/�x	/���D��W|N�@���Dp�{��&ۗ��^Qa����o�	����/ˎ˓�K��xփ��ůT��d{>:�L�r��K�K��Ѓ��E��=�n�}�k#�;HF$����X�JU;�&>�HC~��#8�X�J�_&�[%�J��v�%�ז��G
���_�W*ҩ��y����!%�#Wsm�����k�&>��	�5O������ʾYm�b�����fR��uzJ�,�d�25���SD�8n�Fu�nɦۦE�մ���vq���4�)9B�t���pn�ώ��'��-&7m�BI �Q���
u��2�),T**}Ƙ2V���5�i7d��P��V��0���ܐ	D�L!A����Y��qXɮR���j�݆�"��ҧRv�H ��-v,xd�`R,%
�O�L/�G
� Oy��B�����$��4��S����B)"4I�z�5j����F�Q�Cd�?��^k�`0-d��RԵ�n01��Ѭ�[�Z8�l�L�;K=r��2�tSf��3j`�� k0��gG�aۗ���vwq"����k�Mc��}A��lx`WU]�H��lu�ᆮ�f;|n�L��2�b�Xa���:��~�U1܃K��s:q6�p|l���Z7�O&z���d�E��s��0$j����K4�K�x���)A�nS���JT��?! FtyCqM�-%al�33+N�`�u39ۣ�ě�_�3eا�Xwg"�2�3Ļ��ֲ��g]w̼�i�.����a�	��u}ְOڧ�鼈�F�PJ;������h��Fү�T����0!Si���|��aIe��W5�I�8`�U��E^z=�[��ueL�&J^:�@%Tg�T[i�3�����n�Ba� {�@V�I�Lh���f�*ǁ�s����6$��ك�KG�U�����=In��&P����ͺ���w�J�Zu��=��%��E�[N5yL]��ՇL1GJ�"���bG"S�TTN��_1{���r�)95×3��}@�|%07�q�V���?��r�>v��iu�[��~?ㆈ����A;����/Q��`2�#�L��1S��X�2\��>�R�̧/ƨ�kC�%h�j�VĮF�`��o��ʞt
C�s׆�j�_p���)C�3���#�2.�!w������#1a�k�Kd7仂�㹃���LÆ�s����-V�N�R�	C�?�{�Ui��L��)<�O��sFBd��d.�Gã�՟���Wn�ƫk=~�V��g�k!�F��#����ń^���6��� 8�lu2�of�\*��O%�0.�|�R�Kzq
ŝ�ԋ����kqӌ^����Oj`aV�D�k��b2����,�:�c�,$oL�,M�S2cJ{0롤X�Vom�������ETv1�i�Ԑv6� [	�~�~ʥG?�6�c^S�c��C���{Q[ߏ�M%�ҋ�f�a�}��_쐗��Z�3X_�,�:{p��q~�z�w ��&ź�a���u�{йH�u�$҃-�.��_%�6\�	fz�y{p�\W����(Z�kx��P�^O�3�t�f:E�:�qR��:]I�I���( ���މ�~l�F��4YHT�Rf{]}�Nv`��劯���/�+Mi�)//��tg�w{p�"���W�-�n��?؁|�;a.U��j���ܜ����<Z�/e�5��t���HOR��W�Ȟ�?ۃE��HU7>�ӎ��"��-{�aϡ���-U����ԃ2�63�[0>L����l��\�Â�����܈lG��c��Oq^C�A�!����YF`����r��K1�1
/�F\*�q�l��҉+�\)��*�W��ދ��	� ��Fy7ˇ�E����1n����pGVv���H����R��vL�e��o���j̠ffl����X�1YΔ��񸹜yӈf=��9�b� g�Π1^7-�HVp̅y\V�ͩ2*��|��zf�x��4�*��y_V˗���$�ES52?�����sl0p��1��p����oQ�_�P���ޏ,�8%k?\|��8:���5�6Xs��B�ı� 8 "1̻��ߓ�yF�r2�0���M�e!_�1���-⎼֏}
&'�<I z��x��^���,�D}x>k��
��JGY^#1�c0��՛���+]11o*1�B13��Jy�B~�0bRr�9q)k�[�<*�t܍�^w�l��;���[�N������>���P��Tb�#x(�;a���7���ß�}E�\��Կ)��v.IZ���[\;���2��}�x�s��w���,�$�A�>��z�c$��$&7esF��1hQUiܠ1�Kc
�=2b����r�149�Bc���+����r1N��OX����`�j"���D���m؇���S���I�����Qd?��!�\<x����1�f����/I^�-xEnƫ�^�����M{7�x߆�Ҥ��+�P�c�y��/0o?�T����u�|Eӭ�����۳�g�Lo�%�4����jy��ܢ��y����J��r�l�b������|B4q�L��̈iҞ�������c1�2$@
���è��@�!�x�@�ğe��0�~W�Bb�~�H�8��?�g���A��A�	��c����Sm��~�A^���!ޒ|�ovY8�_
�z�zG������peI��O�0-�+T:Oډ��=r��R�9Y�eW�9��g�@�����>��5ME�&���R��
V��x��[,��h����+��b��XE�J/�Hº:["�K���D�ҥ�X,������~�Vy|<1ڐ9 C;�N��)F�Qu+k�
��,��)O�Q_�Tu�\G��{q^i�cV�S��x�0�_
����x�9���^��N�#%I���W9X�����M�����R�k����L�%Q¡Wu�&��=]߳�og�^�������+��#���h䎮�����|���)ng��gI���r]휜�3D������.�X���Й���9�Cp�U�;+t$~�A�0e�M�s�6V�X�)��#�w���V��\�&�e�̹����̗g\�8����+30�1��ܿ*y*��g���Y=R�
��G�����!��Ky2'���_�k4��r~]�|�N	�sc�op���ɒ1����C�i;Kn���|~�σ���]9r�o��6���y;�lce'���}'�w��n>�ud���܍	�PK   }y�X��sP  �  4   org/apache/maven/wrapper/cli/ParsedCommandLine.class�Wiw�~��-��-�`'۲l��M�LL�b"��v�2X�<D)����I�n�o$�7����(�t�֞��~�hz8�Ͻ3�%{�Ѽ��{��y����>��_ ��w1���E�@�ec�H;�>{�9�
4�l����hh�TJ�*�O�I���pZ�kh�юmCE'�6J����^4�L;}�1J%�I����S6s'������Z�)��Xr��]>�<�:���Nv���j���}�tc���б]�%�,��{�3�h�hh�����q�1��V��vy���(�)���N��H�4�QgJ�m�^$4���8����pMǸX`j͖ݢ#���_ga�ߧ��8��WG��VT��<�xT)��:R��j{G�v]�k���QW2�y�z'cF8�!��x?���͵:t6$/+ъ�%zG�^hm`�G�V<�1�t| �GH�R����6��q' �f���0�8XN%�/�b$�I��A��J�5��ܙ�e��;������hL�8���^��΢����mT���q�Z&v��>^�
9Ӊ!�p3��rqN�4^�cYK;'0�x��A���ҋ:�p��v�Վmd`-�V���#�(�z�
�	��)}\����e�h��%ɶ3��mr�pl�k�9Q,�Z�y�Z�og�pw�{^��e,�|$��� �AMYx��-�7
U���:o*��xE`�D��`�7a�Dμ��sC1�(���n�L+�pE�U�����Š�\N��	�ī���x� �ݗ��9�i+on�!�s�#��� �V�1����Y`[jX7�7Q�ܜ$s�D1'??��Te���M�x�8ofǒk��#0-K��8J*�*q��Z���~0�H�]���yr�K�Ԋ��#&~��X.U�8��aC�j�v����!��DP��`Y�����'��rU\����t�Q�*���(��(�mG�o݀yIq���Um+�Ys�,�>�]��)�|�I���M'��;��>o�������77����FY�1
=��=[�_��Z�d{Xl�����t��̛�,IɮzC22��G#��tqy���k\��)�y!��?�5o����h�3��P�, FhA��k�;�{����b;(�oa;�]�o�g�.�L����u<G!)�7M��0ug"����]<%��vG}����b&Ӕ�b�Mݑ�8����VV�6#��s�b^@/W���~���y��c��i$aa Wx��A�J�ϫ�Q��xM�:�/�K�p��|�2?|_��5|�x|��Χ��MJߢ| �U����V{5��yK�0��p�D����<���$�ѷ�������p��n��g��"R��q��:+smW�F�`��\��~�!y��*�w�l�~�!K��&#c^92	�zDJ>����D,?{�M�?��ѺUxle��ۘ�������sG�{�`�2�g(�a����E��I����?"�!�Q�C�s��B���������2�5��u��?@\CV���Ҕy�>�oR��$�	���L�ƅY�5��X7j�ƔB��M�H����:Ɏ7hN�-r�C�->#|���|�{�T�L%I�K���XY�w�F�.��,)�"�#�.��/)�	��À�CY��Z��~�@G�����ƣk��)��b����,OɾbЗCrx2�w�{.^�(2H�݁���M55d�q��%�0Oy��^����S;Q�R�5C�G/ۂo~�������0��kT�Zî�x�7�������ӳ~mui�9UB�}{]YR�\WV���_�p�_�7�E�/���$kRj`]����������U�'�Y��٣�P�3!jw(���]Q�~�?PK   }y�X�v�E�  a  :   org/apache/maven/wrapper/cli/ParsedCommandLineOption.class�TKOQ=��-��Uax�m��(�G������IM�.��N������čLT�$��e���@���E�wN���=������y<������ ��[|��7+���V��tS�s�D�(!����l�P�,������ݐ%�ȸ�^���UQy����Z�ۚ���x��Yj���W�jj��z���r^7�����M��FKk��|[Hhd#��'#�~�P�7�"�1�!a�a��~�0�
7
6����%���2�:J��5�n+U��psGqt��h��`"ߦ,ؖnV��b7FpM¨�1(�κd4�]��jÞ"9Vj-����5ӂkJ�uLSBE���j2�9���ę�fDvJ�nR6/�;{�������Izs�ְw��br�e�٩�^/�/<��Җ�e���z٪mj�k�i��K�(rK���:�1���E �5ě�@"y�C���ox��������a�xA����%�Ҫi&%�O�����-��=Ӆ��*J�D�������&���|���c���6!9��DS`�#H�E�}���g��<�d7w�q���ǵ�qeR��C��c�\�����!�iV<x��b҇_��O�~G1N�@[/AA&0�I$hy��O�����	}�,9>TzQA��RH�&O�=�5�=W�SBD� �9D҇7�M��&�����S����{�bn7f� >"��tR��q��ةR��Eɢcd�/PK   }y�Xx��͊  5  H   org/apache/maven/wrapper/cli/ProjectPropertiesCommandLineConverter.class���N1��
"��n\�&0w^Ÿ"J�q_��P23����te���-�18Q�8�����M_ߞ_ b'���H0�CM*��1�:�|@�{�x�r�@�g��V��%���j2yԮ��j2�C�XDB�2�J��r��C��+�%OWL�s(0�7I;�f[w��I��9���N�S"�N��~թ4¡�����T�f�h���xc|�o}�k�MKV��-R7��X�n�v=Ӻ#z��}İ�Ӈixk!#��R����<�ݦV"�u����4�v���7��oʾ��R������ {X0?Ŏ4��+&.��k23y��	��Lf�lbf$na���؀"VG��'pe��ه)t���D7йit/�L@�����kX��;PK   }y�Xkn�4�  &  G   org/apache/maven/wrapper/cli/SystemPropertiesCommandLineConverter.class��AO�0��nK��Bi�1��2���\&q &�V;�2&e��M��$��(�j��v�؇�p�2M%�I������=�m����� |�Q+(հ�2C_*��1���|N�s�x�r�@8�D+��JIJ�2y4���2��C�LDBb(�;#X�2l&i�)�Ʀ��ÉK�^�v������S�/GWv�z~���4�=�9f��U��p�Yw��%����C=����1�o|�9�F\�,_�%=�y��O>eh�����u����ڝ�w>�N�#�q��oJ��HsЄ�8g/�Xu��r�<�,2��������-�5�Q�>��u�9fff��pvg�X^���0��,@�|�+�����ʏ5����}���YG[��A�����A��FVu� PK   }y�X��c  H
  =   META-INF/maven/org.apache.maven.wrapper/maven-wrapper/pom.xml�VMs�6��WluJfL�9t\�%��jl�c�qs����X 4��HJ��8�N}����ݷowIE�e/�4��jt�� E*3.������u�1��_|߻�)
�	f�0�XJ��4S��3ލ���@GT zRA)	�Ja_ԆEX�KF 	��=��'�o`��2�['J�p�" ��H�K�ò�ۤ� .�P:
�)[%�6��+�T�W�in+Hn{���R}Yw��v���V/8N��Qw5z��s-��4Pk��\�XbI�ʪ�L��um��o]�0���U r9�3��2��æi�hR�a_UxG2N���zޣ(PkP�w���� ��H�D�`X=m?\�)w�H[��x���;�zVT�@1��	L�|'���&��g�sx?<����M��<�^O�ٔN�0�~�L��'��%�u�,w"ȭv����n���CW��%O�$��,G�%͸��P�*�����-���ܸ��ޫz��i�+%�cj�vE�Q'y�^PE�sv~N�9��Z�-�6��p���ӳ����K�s�����R;�L���M�>Y�5��y��D�̰�8vwQ�g��U�nab7eQ�d]M����)�|��B�=��3e����-��Q�p h=��P|\�Q�-����y?�E
Vb|o1��b�F8g��u�xe�~����d�	�E70�T��vW���@b;��	�4wc�w?A��v�A�x-�
iE�Qw�lM��pД+S�sЈ}�v�C�8?��]jzal�o��e�����>+n^M�>Y��;��*~���F���37�MJ�O�e�����3�U�Q����[G%�[��̳��K_a�L?�㵨y�=Y��i~w��mh:�mLk,��o��T�\q����1�:�iѲV鶐m�aE�Q��y���V�g^�� �V�=�1'�̙(6csӓ}����?�=��t�_��rM�ݔ~�l� �tGn^�Ya�@���~���N��-,
C��Y���c��PK   }y�X�\�@   H   D   META-INF/maven/org.apache.maven.wrapper/maven-wrapper/pom.propertiesK,*�LKL.�L��M,K��-/J,(H-�J/�/- ����%$&g������g�����q PK
    }y�X            	          �A    META-INF/PK   }y�X�1Oe�   J             ��'   META-INF/MANIFEST.MFPK
    }y�X                      �A�   org/PK
    }y�X                      �A!  org/apache/PK
    }y�X                      �AJ  org/apache/maven/PK
    }y�X                      �Ay  org/apache/maven/wrapper/PK
    }y�X                      �A�  org/apache/maven/wrapper/cli/PK
    }y�X                      �A�  META-INF/maven/PK
    }y�X            (          �A  META-INF/maven/org.apache.maven.wrapper/PK
    }y�X            6          �A^  META-INF/maven/org.apache.maven.wrapper/maven-wrapper/PK   }y�X�G�q                ���  META-INF/DEPENDENCIESPK   }y�X���m  ^,             ��V  META-INF/LICENSEPK   }y�X��w��   �              ���  META-INF/NOTICEPK   }y�X�۱A�  U  3           ���  org/apache/maven/wrapper/BootstrapMainStarter.classPK   }y�X܇�H  C  2           ���  org/apache/maven/wrapper/DefaultDownloader$1.classPK   }y�X�4'0  �  S           ��s  org/apache/maven/wrapper/DefaultDownloader$SystemPropertiesProxyAuthenticator.classPK   }y�X�3�    0           ��   org/apache/maven/wrapper/DefaultDownloader.classPK   }y�X�y]�   �   )           �� .  org/apache/maven/wrapper/Downloader.classPK   }y�XK>8ڤ  {
  4           ��/  org/apache/maven/wrapper/HashAlgorithmVerifier.classPK   }y�XXW1�  *  *           ��5  org/apache/maven/wrapper/Installer$1.classPK   }y�X[/A�  �#  (           ��r8  org/apache/maven/wrapper/Installer.classPK   }y�X;n4GR  %  %           ���I  org/apache/maven/wrapper/Logger.classPK   }y�Xb`3�N  ,  /           ��L  org/apache/maven/wrapper/MavenWrapperMain.classPK   }y�X���|�    >           ���X  org/apache/maven/wrapper/PathAssembler$LocalDistribution.classPK   }y�X\�@j#  �  ,           ���Z  org/apache/maven/wrapper/PathAssembler.classPK   }y�XR(��  c  6           ��#a  org/apache/maven/wrapper/SystemPropertiesHandler.classPK   }y�X��   a  '           ���g  org/apache/maven/wrapper/Verifier.classPK   }y�X�W!  �
  3           ���h  org/apache/maven/wrapper/WrapperConfiguration.classPK   }y�X��e��    .           ��5m  org/apache/maven/wrapper/WrapperExecutor.classPK   }y�X��   T  ?           ��Rz  org/apache/maven/wrapper/cli/AbstractCommandLineConverter.classPK   }y�Xm�v��  -  I           ���}  org/apache/maven/wrapper/cli/AbstractPropertiesCommandLineConverter.classPK   }y�X�:�dP  g  ?           ���  org/apache/maven/wrapper/cli/CommandLineArgumentException.classPK   }y�Xlk�I  �  7           ����  org/apache/maven/wrapper/cli/CommandLineConverter.classPK   }y�X��I�U  �  4           ��=�  org/apache/maven/wrapper/cli/CommandLineOption.classPK   }y�X�#�ر     6           ���  org/apache/maven/wrapper/cli/CommandLineParser$1.classPK   }y�X�@Ƀ  �  I           ���  org/apache/maven/wrapper/cli/CommandLineParser$AfterFirstSubCommand.classPK   }y�X�wM��  0  A           ��Ӑ  org/apache/maven/wrapper/cli/CommandLineParser$AfterOptions.classPK   }y�X1�GX6  �  J           ���  org/apache/maven/wrapper/cli/CommandLineParser$BeforeFirstSubCommand.classPK   }y�X�w5fr  e  T           ����  org/apache/maven/wrapper/cli/CommandLineParser$CaseInsensitiveStringComparator.classPK   }y�X���  o  K           ��s�  org/apache/maven/wrapper/cli/CommandLineParser$KnownOptionParserState.classPK   }y�XmKs�  �  J           ��̣  org/apache/maven/wrapper/cli/CommandLineParser$MissingOptionArgState.classPK   }y�XV�{�  �  K           ���  org/apache/maven/wrapper/cli/CommandLineParser$OptionAwareParserState.classPK   }y�X��`�  �  E           ��=�  org/apache/maven/wrapper/cli/CommandLineParser$OptionComparator.classPK   }y�X7A�ϳ  !  F           ����  org/apache/maven/wrapper/cli/CommandLineParser$OptionParserState.classPK   }y�XO��4�    A           ����  org/apache/maven/wrapper/cli/CommandLineParser$OptionString.classPK   }y�XX����  �  K           ����  org/apache/maven/wrapper/cli/CommandLineParser$OptionStringComparator.classPK   }y�X����	  2  @           ���  org/apache/maven/wrapper/cli/CommandLineParser$ParserState.classPK   }y�X�Ć��  g  M           ��O�  org/apache/maven/wrapper/cli/CommandLineParser$UnknownOptionParserState.classPK   }y�X/���x  �)  4           ����  org/apache/maven/wrapper/cli/CommandLineParser.classPK   }y�X��sP  �  4           ��{�  org/apache/maven/wrapper/cli/ParsedCommandLine.classPK   }y�X�v�E�  a  :           ���  org/apache/maven/wrapper/cli/ParsedCommandLineOption.classPK   }y�Xx��͊  5  H           ��c�  org/apache/maven/wrapper/cli/ProjectPropertiesCommandLineConverter.classPK   }y�Xkn�4�  &  G           ��S�  org/apache/maven/wrapper/cli/SystemPropertiesCommandLineConverter.classPK   }y�X��c  H
  =           ��F�  META-INF/maven/org.apache.maven.wrapper/maven-wrapper/pom.xmlPK   }y�X�\�@   H   D           ����  META-INF/maven/org.apache.maven.wrapper/maven-wrapper/pom.propertiesPK    7 7 �  O�    
```

## auth-service\.mvn\wrapper\maven-wrapper.properties

```bash
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip
wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar

```

## auth-service\src\main\java\tg\ngstars\auth\AuthServiceApplication.java

```java
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

```

## auth-service\src\main\java\tg\ngstars\auth\config\GlobalExceptionHandler.java

```java
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
                fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalide",
                (a, b) -> a + "; " + b
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

```

## auth-service\src\main\java\tg\ngstars\auth\config\KeycloakAdminConfig.java

```java
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
        // ponytail: timeouts configured via KeycloakProperties/HTTP client config
        return KeycloakBuilder.builder()
                .serverUrl(props.authServerUrl())
                .realm(props.realm())
                .clientId(props.adminClientId())
                .clientSecret(props.adminClientSecret())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}

```

## auth-service\src\main\java\tg\ngstars\auth\config\KeycloakProperties.java

```java
package tg.ngstars.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak")
public record KeycloakProperties(
    String authServerUrl,
    String adminClientId,
    String adminClientSecret,
    String realm
) {}

```

## auth-service\src\main\java\tg\ngstars\auth\config\SecurityConfig.java

```java
package tg.ngstars.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import tg.ngstars.common.security.RealmRoleConverter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new RealmRoleConverter());
        return converter;
    }
}

```

## auth-service\src\main\java\tg\ngstars\auth\controller\UserController.java

```java
package tg.ngstars.auth.controller;

import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tg.ngstars.auth.dto.CreateUserRequest;
import tg.ngstars.auth.dto.RoleAssignRequest;
import tg.ngstars.auth.dto.UpdateProfileRequest;
import tg.ngstars.auth.dto.UpdateUserRequest;
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
                .body(userService.createUser(request, jwt.getSubject(), null));
    }

    @GetMapping("/api/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.getAllUsers(PageRequest.of(page, size)));
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
            @Valid @RequestBody UpdateUserRequest request,
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

```

## auth-service\src\main\java\tg\ngstars\auth\dto\CreateUserRequest.java

```java
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

    @NotBlank @Size(min = 8)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
             message = "Le mot de passe doit contenir au moins 8 caracteres, une majuscule, un chiffre et un caractere special")
    String password,

    @NotBlank
    @Pattern(regexp = "ADMIN|MANAGER|TECHNICIAN|CLIENT_PORTAL",
             message = "Role invalide : ADMIN, MANAGER, TECHNICIAN, CLIENT_PORTAL")
    String role,

    String phone
) {}

```

## auth-service\src\main\java\tg\ngstars\auth\dto\RoleAssignRequest.java

```java
package tg.ngstars.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RoleAssignRequest(
    @NotBlank
    @Pattern(regexp = "ADMIN|MANAGER|TECHNICIAN|CLIENT_PORTAL")
    String role
) {}

```

## auth-service\src\main\java\tg\ngstars\auth\dto\UpdateProfileRequest.java

```java
package tg.ngstars.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
    @NotBlank @Size(max = 100) String firstName,
    @NotBlank @Size(max = 100) String lastName
) {}

```

## auth-service\src\main\java\tg\ngstars\auth\dto\UpdateUserRequest.java

```java
package tg.ngstars.auth.dto;

import jakarta.validation.constraints.*;

public record UpdateUserRequest(
    @NotBlank @Size(min = 3, max = 50)
    String username,

    @NotBlank @Email
    String email,

    @NotBlank @Size(max = 100)
    String firstName,

    @NotBlank @Size(max = 100)
    String lastName,

    @Size(min = 6)
    String password,

    @Pattern(regexp = "ADMIN|MANAGER|TECHNICIAN|CLIENT_PORTAL",
             message = "Role invalide : ADMIN, MANAGER, TECHNICIAN, CLIENT_PORTAL")
    String role,

    String phone
) {}

```

## auth-service\src\main\java\tg\ngstars\auth\dto\UserResponse.java

```java
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

```

## auth-service\src\main\java\tg\ngstars\auth\dto\UserStatusRequest.java

```java
package tg.ngstars.auth.dto;

public record UserStatusRequest(boolean enabled) {}

```

## auth-service\src\main\java\tg\ngstars\auth\exception\ConflictException.java

```java
package tg.ngstars.auth.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) { super(message); }
}

```

## auth-service\src\main\java\tg\ngstars\auth\exception\NotFoundException.java

```java
package tg.ngstars.auth.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}

```

## auth-service\src\main\java\tg\ngstars\auth\model\AuditLog.java

```java
package tg.ngstars.auth.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "audit_logs")
@Getter @Setter
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
}

```

## auth-service\src\main\java\tg\ngstars\auth\model\User.java

```java
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

    @Column(name = "keycloak_id", nullable = false)
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

```

## auth-service\src\main\java\tg\ngstars\auth\repository\AuditLogRepository.java

```java
package tg.ngstars.auth.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import tg.ngstars.auth.model.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    Page<AuditLog> findByUserId(UUID userId, Pageable pageable);
    Page<AuditLog> findByAction(String action, Pageable pageable);
}

```

## auth-service\src\main\java\tg\ngstars\auth\repository\UserRepository.java

```java
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

```

## auth-service\src\main\java\tg\ngstars\auth\service\AuditService.java

```java
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
        var auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setAction(action);
        auditLog.setResource(resource);
        auditLog.setResourceId(resourceId);
        auditLog.setDetails(details);
        auditLog.setIpAddress(ipAddress);
        auditLogRepository.save(auditLog);
    }
}

```

## auth-service\src\main\java\tg\ngstars\auth\service\SecurityUtils.java

```java
package tg.ngstars.auth.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public Optional<UUID> getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Jwt jwt)
                || jwt.getSubject() == null)
            return Optional.empty();
        try {
            return Optional.of(UUID.fromString(jwt.getSubject()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
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

```

## auth-service\src\main\java\tg\ngstars\auth\service\UserService.java

```java
package tg.ngstars.auth.service;

import java.util.List;
import java.util.UUID;

import jakarta.ws.rs.core.Response;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tg.ngstars.auth.config.KeycloakProperties;
import tg.ngstars.auth.dto.CreateUserRequest;
import tg.ngstars.auth.dto.UpdateProfileRequest;
import tg.ngstars.auth.dto.UpdateUserRequest;
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
    public UserResponse createUser(CreateUserRequest request, String createdBy, String ip) {
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

        var realm = realm();
        UUID keycloakId = null;
        try (Response response = realm.users().create(kcUser)) {
            if (response.getStatus() == 409) {
                // Ponytail: user already exists in Keycloak (race condition), try to get existing
                var existing = realm.users().search(request.username()).stream().findFirst();
                if (existing.isPresent()) {
                    keycloakId = UUID.fromString(existing.get().getId());
                    log.warn("Keycloak user {} already exists, reusing keycloakId={}", request.username(), keycloakId);
                } else {
                    throw new ConflictException("Utilisateur Keycloak deja existant: " + request.username());
                }
            } else if (response.getStatus() != 201) {
                var errorBody = response.readEntity(String.class);
                log.error("Keycloak user creation failed: status={}, body={}", response.getStatus(), errorBody);
                throw new RuntimeException("Echec creation compte: " + response.getStatus());
            }

            if (keycloakId == null) {
                var location = response.getLocation();
                keycloakId = UUID.fromString(location.getPath().substring(location.getPath().lastIndexOf('/') + 1));
            }

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
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(UUID id) {
        return toResponse(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + id)));
    }

    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request, String updatedBy) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + id));

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setRole(request.role());
        user.setPhone(request.phone());

        var kcIdStr = user.getKeycloakId().toString();
        var userResource = realm().users().get(kcIdStr);
        var kcUser = userResource.toRepresentation();
        kcUser.setUsername(request.username());
        kcUser.setEmail(request.email());
        kcUser.setFirstName(request.firstName());
        kcUser.setLastName(request.lastName());
        userResource.update(kcUser);

        if (request.password() != null)
            userResource.resetPassword(passwordCredential(request.password()));
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
        var kcUser = realm().users().get(kcIdStr).toRepresentation();
        kcUser.setEnabled(false);
        realm().users().get(kcIdStr).update(kcUser);

        auditService.log(userIdOrNull(deletedBy), "USER_DELETED", "User",
                user.getId().toString(), "Compte desactive: " + user.getEmail(), null);
    }

    @Transactional
    public UserResponse assignRole(UUID keycloakId, String newRole, String adminId) {
        var user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + keycloakId));
        user.setRole(newRole);

        var kcIdStr = keycloakId.toString();
        var realm = realm();

        var metierRoles = List.of("ADMIN", "MANAGER", "TECHNICIAN", "CLIENT_PORTAL");
        var toRemove = realm.users().get(kcIdStr).roles().realmLevel().listAll().stream()
                .filter(r -> metierRoles.contains(r.getName()))
                .toList();
        if (!toRemove.isEmpty())
            realm.users().get(kcIdStr).roles().realmLevel().remove(toRemove);

        try {
            var role = realm.roles().get(newRole).toRepresentation();
            realm.users().get(kcIdStr).roles().realmLevel().add(List.of(role));
        } catch (NotFoundException e) {
            throw new NotFoundException("Role '" + newRole + "' non configure dans Keycloak");
        }

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
        var kcUser = realm().users().get(kcIdStr).toRepresentation();
        kcUser.setEnabled(enabled);
        realm().users().get(kcIdStr).update(kcUser);

        var action = enabled ? "ACCOUNT_ENABLED" : "ACCOUNT_DISABLED";
        auditService.log(userIdOrNull(adminId), action, "User",
                user.getId().toString(), "Compte " + user.getUsername() + ": " + (enabled ? "active" : "desactive"), null);

        return toResponse(user);
    }

    @Transactional
    public void sendPasswordReset(UUID keycloakId, String adminId) {
        userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + keycloakId));
        realm().users().get(keycloakId.toString())
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
        var userResource = realm().users().get(kcIdStr);
        var kcUser = userResource.toRepresentation();
        kcUser.setFirstName(request.firstName());
        kcUser.setLastName(request.lastName());
        userResource.update(kcUser);

        auditService.log(keycloakId, "PROFILE_UPDATED", "User",
                user.getId().toString(), "Profil mis a jour: " + user.getUsername(), null);

        return toResponse(user);
    }

    public UserResponse registerClient(CreateUserRequest request, String ip) {
        return createUser(new CreateUserRequest(
                request.username(), request.email(),
                request.firstName(), request.lastName(),
                request.password(), "CLIENT_PORTAL", request.phone()), "SELF_REGISTER", ip);
    }

    private void assignRealmRole(String userId, String role) {
        var r = realm();
        try {
            var roleRep = r.roles().get(role).toRepresentation();
            r.users().get(userId).roles().realmLevel().add(List.of(roleRep));
        } catch (jakarta.ws.rs.NotFoundException e) {
            throw new tg.ngstars.auth.exception.NotFoundException("Role '" + role + "' non configure dans Keycloak");
        }
    }

    private org.keycloak.admin.client.resource.RealmResource realm() {
        return keycloak.realm(keycloakProperties.realm());
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

```

## auth-service\src\main\resources\application-dev.yml

```yaml
spring:
  jpa:
    show-sql: true
  flyway:
    repair-on-migrate: true
  devtools:
    restart:
      enabled: true

springdoc:
  swagger-ui:
    enabled: true

keycloak:
  auth-server-url: http://localhost:8088
  admin-client-id: admin-cli
  admin-client-secret: ${KEYCLOAK_ADMIN_CLIENT_SECRET:}

logging:
  level:
    tg.ngstars: DEBUG
    org.springframework.security: DEBUG

```

## auth-service\src\main\resources\application-prod.yml

```yaml
springdoc:
  swagger-ui:
    enabled: false
  api-docs:
    enabled: false

logging:
  level:
    tg.ngstars: WARN

```

## auth-service\src\main\resources\application.yml

```yaml
server:
  port: 8081
  shutdown: graceful
  forward-headers-strategy: native

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
    enabled: false
    path: /swagger-ui.html

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    env:
      keys-to-sanitize: password,secret,token,credential,admin-client-secret
```

## auth-service\src\main\resources\db\migration\V1__init_schema.sql

```sql
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

```

## auth-service\src\main\resources\db\migration\V2__add_audit_logs_index.sql

```sql
CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at ON audit_logs(created_at);

```

## auth-service\src\test\java\tg\ngstars\auth\service\AuditServiceTest.java

```java
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

```

## auth-service\src\test\java\tg\ngstars\auth\service\UserServiceTest.java

```java
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
import org.springframework.data.domain.Pageable;

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

        var result = service.createUser(req, "admin", null);

        assertEquals("jdoe", result.username());
        assertEquals("TECHNICIAN", result.role());
        verify(auditService).log(any(), eq("USER_CREATED"), eq("User"), anyString(), anyString(), isNull());
    }

    @Test
    void createUser_duplicateUsername_throwsConflict() {
        var req = new CreateUserRequest("jdoe", "j@doe.com", "John", "Doe", "pass123", "TECHNICIAN", null);
        when(userRepository.existsByUsername("jdoe")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.createUser(req, "admin", null));
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
    void getAllUsers_shouldReturnPage() {
        var page = new org.springframework.data.domain.PageImpl<>(List.of(user()));
        when(userRepository.findAll(Pageable.unpaged())).thenReturn(page);
        var result = service.getAllUsers(Pageable.unpaged());
        assertEquals(1, result.getContent().size());
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

```

---

########### client-service ###########

## client-service\.gitignore

```text
target/
*.class
*.jar
*.war
*.log
*.iml
.idea/
*.swp
*.swo
*~
application-*.yml
!application.yml
!application-dev.yml
!application-prod.yml
.DS_Store

```

## client-service\mvnw.cmd

```text
<# : batch portion
@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.4
@REM
@REM Optional ENV vars
@REM   MVNW_REPOURL - repo url base for downloading maven distribution
@REM   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
@REM   MVNW_VERBOSE - true: enable verbose log; others: silence the output
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
@SET PSModulePath=
@FOR /F "usebackq tokens=1* delims==" %%A IN (`powershell -noprofile "& {$scriptDir='%~dp0'; $script='%__MVNW_ARG0_NAME__%'; icm -ScriptBlock ([Scriptblock]::Create((Get-Content -Raw '%~f0'))) -NoNewScope}"`) DO @(
  IF "%%A"=="MVN_CMD" (set __MVNW_CMD__=%%B) ELSE IF "%%B"=="" (echo %%A) ELSE (echo %%A=%%B)
)
@SET PSModulePath=%__MVNW_PSMODULEP_SAVE%
@SET __MVNW_PSMODULEP_SAVE=
@SET __MVNW_ARG0_NAME__=
@SET MVNW_USERNAME=
@SET MVNW_PASSWORD=
@IF NOT "%__MVNW_CMD__%"=="" ("%__MVNW_CMD__%" %*)
@echo Cannot start maven from wrapper >&2 && exit /b 1
@GOTO :EOF
: end batch / begin powershell #>

$ErrorActionPreference = "Stop"
if ($env:MVNW_VERBOSE -eq "true") {
  $VerbosePreference = "Continue"
}

# calculate distributionUrl, requires .mvn/wrapper/maven-wrapper.properties
$distributionUrl = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionUrl
if (!$distributionUrl) {
  Write-Error "cannot read distributionUrl property in $scriptDir/.mvn/wrapper/maven-wrapper.properties"
}

switch -wildcard -casesensitive ( $($distributionUrl -replace '^.*/','') ) {
  "maven-mvnd-*" {
    $USE_MVND = $true
    $distributionUrl = $distributionUrl -replace '-bin\.[^.]*$',"-windows-amd64.zip"
    $MVN_CMD = "mvnd.cmd"
    break
  }
  default {
    $USE_MVND = $false
    $MVN_CMD = $script -replace '^mvnw','mvn'
    break
  }
}

# apply MVNW_REPOURL and calculate MAVEN_HOME
# maven home pattern: ~/.m2/wrapper/dists/{apache-maven-<version>,maven-mvnd-<version>-<platform>}/<hash>
if ($env:MVNW_REPOURL) {
  $MVNW_REPO_PATTERN = if ($USE_MVND -eq $False) { "/org/apache/maven/" } else { "/maven/mvnd/" }
  $distributionUrl = "$env:MVNW_REPOURL$MVNW_REPO_PATTERN$($distributionUrl -replace "^.*$MVNW_REPO_PATTERN",'')"
}
$distributionUrlName = $distributionUrl -replace '^.*/',''
$distributionUrlNameMain = $distributionUrlName -replace '\.[^.]*$','' -replace '-bin$',''

$MAVEN_M2_PATH = "$HOME/.m2"
if ($env:MAVEN_USER_HOME) {
  $MAVEN_M2_PATH = "$env:MAVEN_USER_HOME"
}

if (-not (Test-Path -Path $MAVEN_M2_PATH)) {
    New-Item -Path $MAVEN_M2_PATH -ItemType Directory | Out-Null
}

$MAVEN_WRAPPER_DISTS = $null
if ((Get-Item $MAVEN_M2_PATH).Target[0] -eq $null) {
  $MAVEN_WRAPPER_DISTS = "$MAVEN_M2_PATH/wrapper/dists"
} else {
  $MAVEN_WRAPPER_DISTS = (Get-Item $MAVEN_M2_PATH).Target[0] + "/wrapper/dists"
}

$MAVEN_HOME_PARENT = "$MAVEN_WRAPPER_DISTS/$distributionUrlNameMain"
$MAVEN_HOME_NAME = ([System.Security.Cryptography.SHA256]::Create().ComputeHash([byte[]][char[]]$distributionUrl) | ForEach-Object {$_.ToString("x2")}) -join ''
$MAVEN_HOME = "$MAVEN_HOME_PARENT/$MAVEN_HOME_NAME"

if (Test-Path -Path "$MAVEN_HOME" -PathType Container) {
  Write-Verbose "found existing MAVEN_HOME at $MAVEN_HOME"
  Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"
  exit $?
}

if (! $distributionUrlNameMain -or ($distributionUrlName -eq $distributionUrlNameMain)) {
  Write-Error "distributionUrl is not valid, must end with *-bin.zip, but found $distributionUrl"
}

# prepare tmp dir
$TMP_DOWNLOAD_DIR_HOLDER = New-TemporaryFile
$TMP_DOWNLOAD_DIR = New-Item -Itemtype Directory -Path "$TMP_DOWNLOAD_DIR_HOLDER.dir"
$TMP_DOWNLOAD_DIR_HOLDER.Delete() | Out-Null
trap {
  if ($TMP_DOWNLOAD_DIR.Exists) {
    try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
    catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
  }
}

New-Item -Itemtype Directory -Path "$MAVEN_HOME_PARENT" -Force | Out-Null

# Download and Install Apache Maven
Write-Verbose "Couldn't find MAVEN_HOME, downloading and installing it ..."
Write-Verbose "Downloading from: $distributionUrl"
Write-Verbose "Downloading to: $TMP_DOWNLOAD_DIR/$distributionUrlName"

$webclient = New-Object System.Net.WebClient
if ($env:MVNW_USERNAME -and $env:MVNW_PASSWORD) {
  $webclient.Credentials = New-Object System.Net.NetworkCredential($env:MVNW_USERNAME, $env:MVNW_PASSWORD)
}
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
$webclient.DownloadFile($distributionUrl, "$TMP_DOWNLOAD_DIR/$distributionUrlName") | Out-Null

# If specified, validate the SHA-256 sum of the Maven distribution zip file
$distributionSha256Sum = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionSha256Sum
if ($distributionSha256Sum) {
  if ($USE_MVND) {
    Write-Error "Checksum validation is not supported for maven-mvnd. `nPlease disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties."
  }
  Import-Module $PSHOME\Modules\Microsoft.PowerShell.Utility -Function Get-FileHash
  if ((Get-FileHash "$TMP_DOWNLOAD_DIR/$distributionUrlName" -Algorithm SHA256).Hash.ToLower() -ne $distributionSha256Sum) {
    Write-Error "Error: Failed to validate Maven distribution SHA-256, your Maven distribution might be compromised. If you updated your Maven version, you need to update the specified distributionSha256Sum property."
  }
}

# unzip and move
Expand-Archive "$TMP_DOWNLOAD_DIR/$distributionUrlName" -DestinationPath "$TMP_DOWNLOAD_DIR" | Out-Null

# Find the actual extracted directory name (handles snapshots where filename != directory name)
$actualDistributionDir = ""

# First try the expected directory name (for regular distributions)
$expectedPath = Join-Path "$TMP_DOWNLOAD_DIR" "$distributionUrlNameMain"
$expectedMvnPath = Join-Path "$expectedPath" "bin/$MVN_CMD"
if ((Test-Path -Path $expectedPath -PathType Container) -and (Test-Path -Path $expectedMvnPath -PathType Leaf)) {
  $actualDistributionDir = $distributionUrlNameMain
}

# If not found, search for any directory with the Maven executable (for snapshots)
if (!$actualDistributionDir) {
  Get-ChildItem -Path "$TMP_DOWNLOAD_DIR" -Directory | ForEach-Object {
    $testPath = Join-Path $_.FullName "bin/$MVN_CMD"
    if (Test-Path -Path $testPath -PathType Leaf) {
      $actualDistributionDir = $_.Name
    }
  }
}

if (!$actualDistributionDir) {
  Write-Error "Could not find Maven distribution directory in extracted archive"
}

Write-Verbose "Found extracted Maven distribution directory: $actualDistributionDir"
Rename-Item -Path "$TMP_DOWNLOAD_DIR/$actualDistributionDir" -NewName $MAVEN_HOME_NAME | Out-Null
try {
  Move-Item -Path "$TMP_DOWNLOAD_DIR/$MAVEN_HOME_NAME" -Destination $MAVEN_HOME_PARENT | Out-Null
} catch {
  if (! (Test-Path -Path "$MAVEN_HOME" -PathType Container)) {
    Write-Error "fail to move MAVEN_HOME"
  }
} finally {
  try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
  catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
}

Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"

```

## client-service\pom.xml

```xml
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
            <groupId>tg.ngstars</groupId>
            <artifactId>ng-fields-shared-lib</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
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

```

## client-service\.mvn\wrapper\maven-wrapper.jar

```text
PK
    }y�X            	   META-INF/PK   }y�X�1Oe�   J     META-INF/MANIFEST.MF���
�0����]�N� A����Fss�}aA]���|�0��>�=�^Y�!f�<����7����"��VGe eˉ��%-�VIL���V5"�_�VA����s�~ν�)K?7#��P�2'�1*ۆ��k;��H���M�����n�|��ӓ�PK
    }y�X               org/PK
    }y�X               org/apache/PK
    }y�X               org/apache/maven/PK
    }y�X               org/apache/maven/wrapper/PK
    }y�X               org/apache/maven/wrapper/cli/PK
    }y�X               META-INF/maven/PK
    }y�X            (   META-INF/maven/org.apache.maven.wrapper/PK
    }y�X            6   META-INF/maven/org.apache.maven.wrapper/maven-wrapper/PK   }y�X�G�q        META-INF/DEPENDENCIES��A
1E�=E.����N�u���4	i�����v��!?g:�Lʙn�ڤ�*ph�΂F�PJ#[1�!;����V��~^y����ŃU���_߁�����ʦ��k
vGЅ#| PK   }y�X���m  ^,     META-INF/LICENSE�Z[s��~���r�Si��4i牱䆭CiD�n&��%� ���. ���=����dw�VM�5I�ٳ�����E/˝�u�:�^<��?�u�t����B�Mv�������<�h7��W����\�6sc���ʽz���~Z���J��Y]-�7��xws'>��qw}{ws��-~]�SW������	�j.�T�;=�rn��k3�'�	��M#Z%;1�Ie['dW��t���bt�V��Tc�_^>[i7X��{!��pKU��A�U�B��֌۝�N�>hxΔc���X/cO+M�z���w�
P	�� �8�������s+��l��v[z��!S@me#�I�c�$핐%I	Z��Y/��^A�o�i
!�
R����cW��Ҵ��$����a�rxùxg,�я�71ɪ���G3/eFGq�B_�R�W� �Y�*�;�w!#J	N����,`E+;�U�<�׍��+V��N������$ٹe��	�\hЄ��v�GI������%������/i;�a�A���:� �d�A�Fu`�R�+'�3=��6�L\�Z���]�^���&�Q�y|x����[�<�'��$�ְ[	)��GZoU�����kM��[���p4IY�������3�ht�qw�3����r�!8���#A^?P����v��;��Q|�l��p�����cl(?jkZ�����	Q�9|R���o��R�yH\1=��qtLH�^cBR�s� g��'��N����P�n�*-�p��c4��	(��KҘp#-����1b����ZY�<H��M�?å���>�dą�n`x8�[
�dV9X[�BA[/��e��ΰ�Ⓥ�W��#$Sc���
W����h7;� ����$�AP|#:��T�p�~��*܊܅����r��8k� �iՃ&Wb�i|�66|��y6yaX唃H!�K��4��Lou�����NՓ�/ı���0���H��V�R��T��)h:F��j��'2��㤓��N� D��%�"��Ѩ'J�u�����"�����9S6�/�'\��Q6�	�p�H�d�6�
~J�"K�Q���M�m7n ;<x�A�E��z>h#��Z�L���j�De��}���5��i��e�^��f^��˰H5��� 腍l(���uD>��[_`�FW�Ph���d!����R�+��K:"�7@)AZV�"r7���5wTXBJ���	v?V>f+�k�F/2�DAfm�p�rtT�iǖ���ȏ�x�4��`��YC<�Q\��ь����B�M�(P.��#�PD�a�F"��l��"����4���u<v���R�܀���Ѧb�l�PFEHJ���$t���mK��r��7K?����H�p۷���Y��������f&K��TI�H ����� 9�S��� �	���T{�\�3�K��Ǘ�z�'s��pxY[�4�S"��Ts��ᆡۂ�c=��	�%8���+B����@�߀�\j}�E޷�4?b1��ϔs�v�3�J���\�2��`�r�"����K��Y3�]a;�������6u�<��j ~�Q��1<Q���`&�M�>
�ʾo��48�����U+����lv8�"	ɭq���uNZM�Y[@���(j_����`�)_���DVOˎ�q��-��$o���b���n.�5�?�B�
c::e�[VAn%�L ���T�"��ƹ�d0<FiF�O�</E#�n��Q[.`��|�G���QM`ŝo���29����S1LŦ�(ShF}��F#�/y�Uqu�E�X�.�
���ҰO�
���;�O��u+	َQpPn3��gX�i#l6�Q!���7�"O�f.�O Y�Z!2H
�V)�rm艸��z�셼䓎i[���~ܪ�Z9���!��TR}8�$��2��d{��&Qi죰硎���Aw'�=�l{���([�-C����e��U$Xxs��Sw .�8n���Kձ��] ,V
yS��	
�!��?� ��s�����g�A�U�-T<&��3��p�INK��h�%�V�o��ճ��������q {c��=�rg��ٕA��L9�,�+ZO	>���)��Y�"(I��fb<�2�A�ŗ�5s��g�J�2%�S���/I�
�6}ԔA�d�d�IT�gu�>�I��y=@	]'����M�T��ũ�e�zٔ��g�Te
� �Y �V/���s�0#�P���wa�_�f��M�[�8�"5��P����"�:Lf�lȪ�[�w�̤ս��$
��G�g�~
�U��jlm�DL ���;�1��`���D�*虘��8��0O�[�5Q�*��Ұ�	���+s
���UƑ�F�:a�g|흹2b1�]���hS����Y<<ъ�ӹ�J$�ΦyI��۪I��g�D�1�&c�ةu�|K͎�	�^5�@7:�����a�Rc�K��8�8��l�����]%��;r��m���Ӛy�Ejf�"��V���ׯ̀���՗���vK��R͍P��_ad.�1��)X1�D[��(�>C�#S��� ��7Ī��|�t�{���?�3]B΁)wv#���jL_�5�lqnN��}����:��C��C��6ժ�F�o���;�	�tr)~���4jV�Q��+bӁ�ړ�lȦ�7_Δ �ԟ��J;j��Ҷ��]1	���7��yc��`��H�K���a>�]R�uš�q��?��ˉs/q��?[��r=?,��u0�����7������bu��^����Z��X�~_����h�~��K'ф+U6&MDsRp� M.��"{
�`��������z�\��[��z����?]߽��\��|����B���~u���^�������N�~���Y_s����o@�6�t�@73�N�<gMo5�s:pх�P�%���<mt87��v��Δ:���������E�i3˱��9|&�E���.ϗXyПn =X|�аt�N;���,�!tj�h`_��,�mw1����g�������!BG�mq�-����v�|~0zN�e��M�� �V�r;�����J@z9��
�ֳ�gH( �|���g�x!��ƙ��j�w�X�c��[��F��9F���ygf��O.��Z���5���&�~��l�^�9����R7��j$�z���"x�M������+��q��x�e�a��4]����� o��r����\,J�	h�����"�,)>O��������B˝1<�I�䲝f���jExPGʮT|��Ǡ�w���Ւ4c�6Awa6��Boy���̗�Z�<�/���Ac���c'ĭd4�3��Go�tMv9���!���4�(�KL'ݢ$DO��,�L{&]3>c�s��m�h�J�Ю�
`�ՙѹ�-!Q �ъ)�Gk�m��&CW��*Q�ӹ����F:�-�l��>�ƌ6F]8��WWXWϽG�/no��?ߠiZ �z�/���o��>�%���.(�k�iB����B>��F�:�Z��r
$;��o)D��_g�h2��!�����Nz..�L����@��A��.u�Ԧ:�	@��;��vv7������"��zV p6/��i?'(N�r�@�!c嶋hf�q�Zݨ��
ݐM.��r4�F�a���|��_PM<���½kϤ!���o�9�e�/��U�Bz��G�����>H��g��O��*.������("�#\���<�x��6��1FT�8"u�fC�29م@�C�Ͻr���j}�T�%_�П���3���N�p�K������H��&�������h��ێp@	�,t�o��iI����\��PK   }y�X��w��   �      META-INF/NOTICE}̱
�0��=Oq���:���Ap��|4g(��rm�����_>����(B
WTwayi��A�����0D����ɝ�VQ	z^r@K��sCLD9,�A��*P~6�J3@s�g��frj�Z��/�S���PK   }y�X�۱A�  U  3   org/apache/maven/wrapper/BootstrapMainStarter.class�Wk{�~ǖ���	�pJ��M�eǖJ 4�!B��vj�R��t%��M�]e����Ҧ)�׶h�Wz/��/��~���~)}g�U��:<�#��9��9������G �G1t�SAHE�m�-S�̹�d�^t"#�i8�t��O�E����n�C�=�ѪZ��g���,�Z��ۙ�,˩9��k��w4��m��f����&�c)����iX�Y��gNhNy�?��`����ɜ�ʍV�Z-gi%�V�M@m_�F�+�Q�;�5
��m�픷�+;6ÝH(إb7��+k�M�6��(v�U�T�|V`���l3U�gNw�j�>�#'<u�%\��S�+�vQ��f�������S3~S6�$C8��
U!��ɲ�k%�-ź-�{��]�T�y�c���Ψe:���shWjS+�xP�~�O�h��V���}�CQ�-Zv�TKW����rF����/� ��P��U��}��c���0���
��DM��y]�Ȉ������X�5�Rw�Fn�Lf��yT�a<&u��^�1jZ��xB�O��F����	�b�8�xqk���(�Q};rE˜��9�]0���>� m�P1�����] �9v~ף�ͳ��d1��W�i��g�`��K*�q����S��W�l<�!h����l�M)��@�O�i_Vq_�s3F61�\�.�ك�{6Z�o�&�#)��/��)(y�� ��ɪcXf��SPVa�<�����m�7l��l�}_`��m��p��(*��Q2Cm�H��li���B����[{e]c�=����:E�������o�͂1xh���W�1*��&�
�pI�WU|_������[�j�5�tVJ|Cųx��1]����~Sŷ�1.V�����^|��f-j���|G��x��P+���j��R�e�S� 	v4�� ��2��sĪ��'����.|?P𒊗��\�rVQ�0=CS��,�xn���5��Q�^)%M�IVHv�S֓㲡%�^�<vx*i�Mj��)l�r�d_o�/��#?V����R��Ef-{^#��S�u��O�	����a�6ϋ�����Y���L�'���>)��x��Ok�!�Mb�),%��\B���|�w��fx��i�w�rw8�Ãi��$wY��M�p*&|u���7�晶}C+t/��ڮ�;#��٥�[�z�#�Hͭ,e=ો�JK�_�%�{��
<��i$Pi��[��Tn�^�0�U]����"��d�_��U����Z((o�R�׺(/��B^���	g��cx`�:_:�S>#.���6�.~�Q����(,�ɧ�vy`p��l���mӝCk�.��;�{{Vp_6���Dh�D(��2�g��k��k8$���3����5�w�OO��_���tg�T~g��DD��1ݹ��3�]�����Ѱ���x���^^���}&�/3��\��s��:z��L�Q��K�v���_��t:����:��)��E�Z��w�=��m��H�?5ak���k�"�:���	�EF%�{����`�MW�_�Ӄ���� ���Dh��p�|{F��g�C�D9.3���9�kL��=��;���'B��2�&B�����{�W=W��
���V��A^z���s�!�4m���=Ȓk{0BT&�F9���+�;�7Z�<{x���Zvs�����-��������E",��ɱ!�Ғ\���!�	��(�*��?~B-��7:r
�|���ׯ@�%C+���n���/PK   }y�X܇�H  C  2   org/apache/maven/wrapper/DefaultDownloader$1.class�TmOA~
�ak�"��R����O5��Zb[�m��.��.�^!|�'��h��Gg�HP �Knvfn�yfgg����?<�3CY��.��͎p�|OHw_��]�ݲ���}9�F,�:�0ư|.� vx?j_���N�1����^%\����������+E���aG��o�Pi��ܗ~��0���-$f�C����W�u�+9�$�d�^i�[+nV�墍똲�r0��(C�eۭ�B�%����cXȔN|�/۹�i�n�%��k���͂�۸cᮃY��Ka&B�u��k�b��g#�����gPoy�:���Lk"橖`��|)*���o�v@�dI5yP��7��3f��a�,��,jU��]꟦3�ȳυa>s������5��M��7��N�����l�G=�`�Fq���;xb�n�����k��Ά�B{�)z��+b��b���$��;�4�H[��x�ť/^<���(f��(�xH25�BY �L6���
�� �%,�ˤ}E�O�@�]��7\Kΐ��[����!Ia�GLL�H��El� |�6�Gb.�1��1�,L,���PK   }y�X�4'0  �  S   org/apache/maven/wrapper/DefaultDownloader$SystemPropertiesProxyAuthenticator.class�SkkA=7M�q�>\5��Z�Z�X��VQ"BI�TՂ��4��d7�N��W���?J��Y��pv��9���wf~����2��H`�B2�a��UwE[��m�]�{J��R����4�j��7�#���A�ek]��=r�����k�&t�#/<��/	C�����ѺD�|����v��-�����E�j�������ŶQ|Jec�.���
a2�h
���LF�R�V��)�V�_/�ϧR�FpN��J!K��2�7��^H��-ܶp'�i̜uع*�V��<��oUl֞5;6G��Qު�7Z	v$a����M��-�;���Sj��)�g�q2�^Hx[��'�L�2�ﻓ��\ܒ�o73�r��*���'������F�Q5��3ɜ��WB���KUi�_ZxD(\\�0��YxBX�<f�"�`�!��2�0�3�G>�.���A��;%�b��� ���1�<
q�g�Z�;�N��q窓���!n���c:�ˀC�=���p|B��QO�1�s�/��+�ľL���QY1»X�����/�r��oPK   }y�X�3�    0   org/apache/maven/wrapper/DefaultDownloader.class�Y	`י��-k�bB@lAJ����C.;68��W-cc �A�yF� w��&i�6=��^��3i�*��p�݋�v�{�g��}d�nw�f�73�%[N	�y�~���+ v�c
�Z-{&���Ԭ���Ofⴭ�r����Io0�g�hPԠ@XsB?�'��9�9~�H9�{3fƹ_���u�!�R�i���V�ט�Yg�u��Zzڰn�J6�ҝ�e�s�@xp�]ұ3�L�d�F�ZI=\q`°�|��ѰN�zS�2�33���3�g�0�ܸAC���T�X�GET`�����9I�@ްC؄��E�f�\e��|�1�V�RIg^`G�rZ�O)�E`��[k����1�KU�(i[�۶	�l��;{Z'B؁[�hhE��Wb�pU<By��)l�]��Ut�041<y�@�l�w�_�2�!!��󔊝�]�������^�t\��!t������ќb*�[���"c%�3Y#1�1O��<�t���W�} �#���O��g2y�/]%s�6���l�������Z���UЧa����4J��6�K������A����C�/8�����Ti���X���1�flF�eg�6coJ���ԔNŠ@���|�Q0�����906�i���d.X�R#lB�&q�:����,�X_�^Ux�@�{c�˘y#�<cF0��N�9V,�#$FH��K�Ů����f=��g��X�%�o||4�QpT�1�M���8h���<8����K�cB��RHSݒ\�cئ���+"�&�e~Id�f@458ց1�cMI��A��N*�j��Y�n_��c�)[I`��k=R朆GA�6��t��Ϥ�S-KRmɗ�L�h(H��Sz6U��29�ΐ���LKr��C���|����;*3Y]�b&#Z�|E����j�E�;YG�.�2ٴ���AF���a-K��Y���S�ƻ	V	����e|}��
����>I��K�XvJB� >���~gY��}�v6AD3d�I��29�-Z|L7�K�⯱З����\f?��#���jg�AK*���!|�PpNÏ�G��Ey�=k�UF،i�K�e����S���3��F����H����K_|N����`y+{��ee�U�D�s��/QF�r��0tڤ�d	�X`J��e�e����R#~
?�����y�X���C��vzq?m1�?:ػ��X�������C[��X���_��U�@��8/�PKMy�L֯ ,�׍��"I�YҢS5k��R��K��5\�\��Lm���7|����,i�֏gyW�s~����i)Y����� l��B���|�qM���%�>mdٵL��������d����_���[���i��[��j"��)�L���o����d�p�iLr�m�#[)�8kѝ��͔�6d��{~@��{�z��F���;I���?R����v|ɺ�u�u��أ˩xK���r�!���7�
�R�_��5ϲ�˚������]��2�S<Oi[������5C�F��A��2j��mc:���(��?�Q\��6�����w0���.Pw�/���*�/��o�2Ati�<e�d��S�
~���O5���_���;���-eT�fu�un15xc��������n\����v[�i,5��R߼#;��-+i=ܧ�����?�2��VE�������e��3f:�Wwtg>g�2�b	�M� �є�g쾌���h���T�
�%�
���Z{ٍ-����g�m�b^U7%�L>fZN,��`Q�t�s�V������*n�-C�{��	�	�1���b-o�-{^�(��&���%�R�P�N��Bi�j��3N��`Ul��7~jnq�&V;+7�u�b3���b�[���l��"�*b�&�����-�n'2c##���y�R�*ZxܞOl��Z��{��[+^	"Ѧ�v��`pڲ�t��,�����bSԣ���֕/}_{���`�pa�a�K/IŤ���c2��f���P2g3�R'��OE-5�7��,8�����M�%нZ��VY���h'	5c������-V��.�5���&�4��q�H��\O�قk
c.�����w�,%KU4�j�\��,��R�۸r�%��\ZګxC��ː�_�c����lF�Z?X#�6�U�1XBD�
��5^���t?N�9.��l����PH
nd�qZ^Z��T�Zy�cW�����e �X��&e���.���i�Z�v�	y��-�����CX~��\�>AS�\Mp��z>��.����4�u��9
����T'n��F�� ��Hb'Iu���؀]b'Wb�%n�����d\���A�َp�\[n^��kX}	�)q[�]�wy�vi��Sw#�n������]r/D�ě��g|����輆�������˒Y���U�l�{-w?{ ͼFl�@�m�L���H�>��YNIi�:�/���	��x\�ѿ��&��1TD������w\��(�f:�*��5���c؁$�1A�OVع�l�.q����K�-�4R� �͢�}B�>U�T�JO���Jcw����Ga�(��h |*�P�O�Kxl�"~����xW�=E<���C��ß�O?�">[��`4Pĳ�JT����S���\8vTN'��5�*E��6w�r��jݍQ5�X��Fի��~q�9��y������Q<���U�6��|N�K�@�.�
t�l�1�8D���G��G�ܣ��atb�8�}F������@Ӥ=C�iҜŗ(�|���8�zb��;��E����ە�O��}r��	y�(���w����M���%��uF�q_�������� �`X�n'�+w�/A(��6�����&�ۨ��g���=��Q�~�|��|���_�/w�,!�5���ߠ[�:/�<�K�e�P21�z-�]ôyD��h-��U�S�:��*���*̸���w�q���	'�iT�rζ�E��PG�ϊ�F�oN��w����!�^���[��݁N�u"�U��hS���?E|�;^���wD�|#"�Շ�Q�@w�P�""�(�Uʩ��4��O�I>=�t1l�wr�q��	l��]�OѭOs�{���$����8�~�bG�R�����83� �<��ô�I4�sO�1(���dt�M&��ek�u-�
�gĈ�B;�^��.d�o �S�!P��;K�x��1r�#�Νn2rS�p�5�̄����5�Eqc�����E>�+�7?J�|���qt�\Vv���)��b%�v1��E���U�E�+��K�:yr~�GT>O�}�-_D�g���&>�#"�Gw "6�gCD�E>�q�|*�-q��u=�7&��ɩ�hCr*&��̒SjGrjM��?J4�\����q���S��Ӑ��"�,}�9֏���_`�|��2(��m�Ÿ�+��W��	��m¨��[�T���n�h�n1ŷ )�-�M���aW�#���ױD��Q�'q��?PK   }y�X�y]�   �   )   org/apache/maven/wrapper/Downloader.classE��j�P����

>���lܹm���~LF�����u��C�%��a�p������Z��u'�B�D�,5\9)
u�l+�[���F�\�s1'~?d��~\#��l��-y�y[�*�|Ls嵔�r�'�/_�ej�G�¸�zw<���.�WJ6A�nt�@h�V�su��:��g�w���PK   }y�XK>8ڤ  {
  4   org/apache/maven/wrapper/HashAlgorithmVerifier.class�V]wE~���4]�J�V`)��Z%A��@J��B���d�lIv�f��
~r��x���xQzD�o��x<G~�zA}gӔ����vfv�y����y���x��h��ï � C�>�Ǌ����LN���<j��s�����B#GXA�^�6��ذ�T�8e�E�$���I�G73��`$�Lyڱ3���^� ���a��0+�3�"6R�H�1,3�fl�hQ�[����Pa�h���Yv2(��В���SHL�ֳRs�NV�I��m��h�_�a�ӔeD&�I��K
v`'Qꖳ�C�p:J&%�۠)�-S�����SS�6Z��}�(��D��}�ϰA�f�n�lm�,m_I�D������&�٥��#щA�^���3�Ŭ���ALVF�D��!�UW��a*�W8��׈�gg�Ȧ��+FrDd0�Z}L��A�=��u[UuU��eβK:yy�N�L��/�Dt-Jz���$uz�RJ��_�k���<��4���e:�tN_w�"�V�JM'���]A���^i�$9�*8�T->�1����ыď����(CK���D��czѐ5�պ*ZA�4"D�K���Nh��JT�xA��ù�Zu���4F9�V0�K�,;��z� b%}Z��[rj��|^��a�,��z�q�3�?;�蚎Q�od�w�B�`�nH�˗�!N/��K[Z֨��IWB��m�r�zg%#_p$A�T���A��OK椀V�uٕ��®���P3E�x�9�U�"c�jH!����q��{q��=��u�9C�;Q�[�Jc�l�a��251z��R�lJ�8�&�=*ﳬ��1�6��Ҧ�)T�����M�.Ǵ\�2�}�����D�H���H&�,
3�X�z���l����[�W_F�~���z.2�j�Y�S+�ah�k��mq��J��v�k�{ٕ�O*�8t�<u������g��e/-S�Ok�f/����U���	��T�1�E�^7�}R���Uj��Sg�35t'�+F3�9p��]�8Cc��܉,�J ��yE�q�
�=�] ��}�a����~
ŹT�<Z�B_�Y����G����j�1���y��N�|��K�J<�r�p{U�H�#�Gϸ|(�C�D$�p��7n�6~��j�5|x�I�-��0�kmZ���������a6�������?�͖᠇�M������O�\�î��<.!Wːyz�<�K�7��S��7�<�>��A�}��V{�F���	%�\�&��5�;���4K	�������m老)pJ�f\C��n�Y�`��;tn�L?�������u�^������+�V>o5M+?I�F~�P>u|�d#H�na��r|H�,�O�/��6���=�ѹ��r�*�-�x����!�����&G����"��,���O��3��>�PK   }y�XXW1�  *  *   org/apache/maven/wrapper/Installer$1.class�UISA��$$�Q�QԈ��w��X��@��lghB�0��� ��'V��Ej�KyPO�Q��;!@����^��}�_�y��ǧ� �b,�D√���!��e�W�� �%�,\��+�[y7��?3��.� s���rH�a�h��px�/s˕�5/a�ʥ�#nҲ(z>��HW����l�@
q�M�@'�΍��/ �9�P0��+l4���B.[Lb���mb�2toF��wDPuB�������Խ	
��[�\�:7��ك&�q��/������ar�Pk2�'VlQ	���MbP\=&��(CrY)�C�7C_������@��slU4�f8ѷ�"����E�Q����.����T����N��R��¨*2��m�g�)��m.xs3��KT>�F�$�Ӯ��@J�b3tm�p��ِ�n�J=���^�Ín��{L����(�����p�lM��T�� ���]|rV�]V}:�B��zd�K%c֫���l�q�'�9�ʄk;^ �r�v��p�yE����E��n�20�+�q���C�L�z K�U�U��6#y�V�4��l������ڵ�8��`4>A/��kV8�>@�bZ�U�d�8MjuXN�ͩ��h��������$݉(�Oi|F�='�����ՠI�:�1D�'53`�"J�S�RG�O։��NY;��O�|^��J4�8]�J��3Z��ct�sd�������}ž��8�Ʊ>c�^�fl-Z��$���z	���
��@��v��Kz���6�[����	=?PK   }y�X[/A�  �#  (   org/apache/maven/wrapper/Installer.class�Z`Tՙ��d�;3��Bȃ"�Y�	IPz��$�����`�[�V��[��Jڊ�<d�nkպ�v������vw����]�[�߹��$���*s�=�?���������z�E ��y����Q�B��}��&d��k���gvڂµ�p�^'�/_��^E���hw�1:{̚^������i
�l#2�w���,# 8�9熆$��S�L�13���v�Ź7�'����:J0[0-b�=���ٻב�<�����u^�a���:|8CP�sێ�s��
vǣ��ݦ������xbff��fǰaӮپ��9�,�0���X}���5��DK�T$~����0�د��X��s���c�:oMk�W0;u���V���y�2�kX���9|<�@�{#��U�ְBY�F�tj�����V�|�~Y�lu�Le��J��p���8Op���H�w#C��,��U��)g��O��c����["���VC����PP2�N1�	<a�n����*�vȮ�\�-ߩ��s��l}<b�x��n��Of������e���AG#.fv+��s���eb�$�r�QG��z�lF��	�6�hq(&�ɠ����1+�M5��ڪ��l�,	�T�uh�Ѧ��m[�E/ڱC��::�S� gd4[��*l]�p�%(�f�v7v�T�*�*�ԱG�WD(������`EĈڊ�SG ���Q��0[�{CN���6��xPaխ�Ar	�!�6��R!4'k-�Y��i�U0��^<�%8������5D&$�+2��Q��i�u�q@�K�j�����5nm��и���ֶ��KXU�?j�c�~8H��Zh⎬��J�e���O���J�B�1��u�Z��z�\�3z�civ$&�!�s̮+
�o�GǮ�qC0��5�*X��Ѝ�	�뫹���:���(�SH�'�Sf4Nefy�R�n��^"h��t+����jM�y*~>��~��S<�������V��u��tq��+Z��ѶAe�J�@0�Y+:�W>zXǗ�q7�9�RΤa��u`{�`0Q��x�'�m��tT���u�v\*�n*g�)8cT�m��5�;�d�<�oi����p,U�A�*�۵m˖6�y��FpE�_vvl�?`�1?k���
�F0�7��&Z�o�ph��]�p�o��vh��2�J����èˊ��Ŧ�L�]Sr&�70�:Н*����s��.��� �)�ӕGN�����ܘɚjv�m��}���b�@L1�����%0<h:�U�/v�X�P1Z���f�+hƦh0�� �<�Ѩ1��ۋW����/u����C�f_Cj�al�ΛEғ�1�~��5��`ޤ���7t��B��`,��������Oh&#WS�۩�yK�?��BAgȊ�~�R�!m�Z}�^��?��'���8Y��#I�s3��w0Y�k��
 <���ꋹ񯄆��pM��o�����Z����羅��G��^����?u��T�5�a'�N�Z�`�*�qC�Z���xq�R�x�մ��{���!�0���`�H���z�o�Ĝoڒ�7�OP����N��l�d*�������Z��{�sW���L#f�vO0�=Άq���Xq��U��I�d�
�P}��C�M���(O>M�[�a�=^��$_���V�l�7�$m�Z�-��w���=)�.n��y�[	D�եHx�,��f��w	�1�/���s�t�ܗ)�m�.����+���33E��cD[ͫ�f��T���^��/�KVz�D�i2G�l�Qz���O�3�L6�}Fh�
���I��Rmy{0T�6k1od�.�U�ʛ�<DT���\5�[M�*�6,�e�,!��V�v��͒�����Y�t]&�U>v�Zqu�.RIӛa[��7�Ah�{��u�H�e�&5��� �4+o�l6[Q�1d��kLm[�˹�](R�BrA*[6A�F
2���	]�W7��x�r�.u����7Q�d�hra���a��C��lg��pg(`�5���Y$�i�~��ň:t�����]w��0���l��΅Wʨ��70M�\*�h�N���ᤊ�`۔n�9�����ͺ��6���)�۩�U���������
5�2]�I+1��]�������+��&홨9ʏZ�-�t(0H$�[v����^��+5��6D>9�"���i� خ��6VpܲW�Na�54^\���mOo?ۚ�ook�Ҳgk}�F���6b��I�W�)|-����4�k�`��h�͖x�^3ڦ*��IܶѠzON�TU!TN��g�؍��}��GY��{#�/:���O�����c�D�3����`쥙Ƹ�y���Xc���f�&~����?��t�a���YՇi���>8wؙ�o��ϱ.��ܼt��l�XG�x��ܿو85��BeڍB��4���c���
e�r4�P��,�������9����Æ��ĵS���dU���J������KG1o��S8��h��Fw��^�jtۺ���<i��HTh�T�K�)3Qˉ�h�3�;NY�4+4S9����υ��DI՘ �����ԃ��3���N0��۷��Vb���]�����\2)��v����m���N3q��48�P�i��p�`2�f��	I�����p�����b�W$�sf:����ZLL�t�-�].>gWA**��VQ��UT?�Y�8�'��-���b�P�0k��B��lEb�e�?��s����:;�g���Nο��L��A�n�ϙ���`������4�Q���T<�Y���-��Zm��.u��������Xt�:�pQq� 6(�KFp�`[�m��׸�ݱ/�cwT�	}Zrާ��
�C�'I����C���F�J�U�<#ؗ�ǧ�^�Ց�4�jFߎ�9W�q;��z*|�!\S�-uA��~��;�a�^Zped8ͧ�\|���>[ Eq���W4�C�t�}A�l��S
|Q���շ§ᡣ�_M}Ei)��Gիg�����)Q��u��xO?M-�:�}*���OT���#x>O	4�Վ�`z<<����d�xJE�aa/b���7��F��%��F�¥m�v4���.lA[1��p��Z1�6|�o��st�m���{��Ӎn\)%�+e|������ގ.ٍnك������'G�_EH^@T^�-?A\~��N���x=��e~y�a��9�U���#K"N��A��(��+���،����s��B9)8*@��H���X%�� �i���oK�\�N�oe�|J>Mn���_��k9sg�`�)���z���o�Vj�4����������9����P�>���Xnt0C�&Jz�G��Mi�[!W����6W&��0�*��ZK���q��q�L��qY�+H���0�*_�~��GO�Y�2�q���b~�����W��\��A���:Q�ϟ���I��>>qR�� �Q���)ב�z�~��FF���sw]�����j�xJ�Z���W�g�V�����߱1���y����m�O�]�5e�s�������CZ��=�5k8y��X�)oJ��=q��GnL��2��~*I�@��@�χ�:VY�/#��<l.~g�n����8�%-������t>�1Q�x�Ϙ�y��ݤі2��;ZwR���z��^��O�CL��P����B�C�����]��֧�^�v6+����A��y/�H�i�ݧ�o�&��5��S�_b~R�f�95��԰�i)Ose�L�O�+�ס9�Ct�,K�I���FsH� y�{{���bTQM�<.%CR:,sw��af��3xNO�9]>��u�|_Opϻ�sE\y�jD����#�P0$g���r�
#R��q5Z��յ�լ	��q���V�i�#rA5\|\�v$}\"��L�$�;$�P���Qx��c�s�����xX6�`q����s;�������/c��=(m*�<�uv�U'�{|^�t�GO�+�b>���Hg�-P��g��Ǩ�!��	�3�q>������x?M'iSx���)�e�އ�6#���	������	\���v�D��'���%��_ë��~�8���>��r�<�>���F �����Tȃ��2�!_�/9��Nڝ��CNԪ��*j���e�������[����Oa��\Y�I��ئ�j�zO����$�:M�X�x��$x'!J��i��	��%#������4�
m�H�*�ic������:�Kdg��~F����б�Jr�����cN��ǹ*y������PK   }y�X;n4GR  %  %   org/apache/maven/wrapper/Logger.class�S�NA��-l�]n�� r�Ee���ŀ!�Ú6�3�aYlw���Q|0Qb��C�nT(�Ϝ9�������/ �WATALCzw�>7jܵ���]Q�z������l)�8
TIhS�����a���p��!ӳm!�������6�ǀ�AC���>7}Q'
o�Sf�q<�t\����\#��Zqu����漫�
�
F5�a�a��#R��m�|����)j�*��vsٮ܁�	��쨐2�)��J��Fq���%G�4C�ҁ��r
�q�e�
�b�C{D*��kX��m_��]dg/�Td��আ[��0tV��y5�]�jp��n��&C��Q0��+�{�-!��Etӫ�Z�K'������4���,B�Jwۻ��Dc�7��B����y�S�7NH����j������ɪXu�t�E� �.-l�V�a<XY���N�N�yYF�g�;�!]"B�Fq�N�U@P��&p��M�uRw�F�Ч�~"�.�h}C��EC�~���O"���Q<�#���Z�&�'�/������C���a3�f;`�6lOO{�3T��c���P�ٜ�<F�;�PK   }y�Xb`3�N  ,  /   org/apache/maven/wrapper/MavenWrapperMain.class�Y	xT��o�&�	F�d�	�"	H��	d3��5>&/��̼a�M�V[�.jkKWk���E�I0E즭ݭ����k�ͶT���&��d&�������{�9�Y���=�"�ω�*�S����q�6��Gk����@~} 0w�W�;�@���B�Ft�V�h�1�6������Q-ѣ�r4`:�@X���ګEJ�+ڭ���v$�k�5s�N�_�b	��&����f@�	x�3��*ʥ�D�R��a6���LE)�,N��ף��A�ΛQ�:�ό£u
������Nq+���j�e��֩݉c���k�%)<,P]>��'v��"֩X/5v��=��FlR�YE9*�fuB�1:���y��A-3mM�V�JA����A��h��CE����3����`��e�-*�b���-�m�,-Q#������ۡE(p;.Vp����.ʴ+m������<m��:��%P��+D�f�W^�=
T4�I�<������qӠ�W�сt�P���, >W��W`]V��p�ԂA=��r�KV�f}D��f�p8hhD�m�Ih<|؉��]`s�d�N�����j�����c!\`$ ��Cf�+6e](����A݊^��^�x�,���m5B���*�r�/�_����f�=x]v�M`���e�����C1��}�5屮D��5*�p-��n9�h	d����$�� O&,�4d]�j�#�����p{ �7�q�>�&}�/)+em�L\8,��aLE 
�Fx$0�Rֶ�Q����!,�>4�����]��q��|���,V
��8�"+�	x3�%[�S��ɣ�*�����9������I�$vg�8�x��x)�%�&�th!��
^�����t~���T��q��<{K/�7�����\��D� k塂�|��������@�U*^-�F��cZT��z��;%��T��;��.��xk�U5��^O�#9qoT�&o�[�VK�jT�5�������B瓤.oSq�����}�A���P�N�»�̮m
j��m1�ʤ�	�f#du`�f�v�����hm:��^�O��U| X�0��4úψG�D��y[��R���aQ�Q|�e2��vï�VgV�lu������>�O*8��>�O��N22M���m޲6F§V��|Gæv�{į[YA����Yk���f �'�xS����MP��zǢ�a�5��ǁiVR�H'N�a�U<"��T5�l4��|�gU|�'��Cq-Kk��\qe�c
���Kx|~إ�d��������mO��s�+���p�0��:�u�)�헐�0v�l[܉`t�Kw��Ys��ZLwo��\#U�����T��!��W>��xޟ�<󹤉��&�p�I|[�wT<����Vʥe/9����c�����6T�u�$��Q���j�|8���Q��(b��$�{�?ď�P�atJ�6�x
]c�\�=;R��0;V����1[�Y�6�Q-��l�ρ�
t�(����G�xx��|O�;W>~��W�5$��Y|��+��,���}m��S�;���D�>ʦ��9����4�	��XD�ˮk��_$o�Q�W��y��ŪØ�_�Q��w��MbG��[�<	&'��?�Kſ�}�$����A#�+����$���ee�i�H�Y� K�"rRev�D�1��xp�=7�g:a_:�A�� ��u:Km���D�m�8{��1���sAU�t4�{;��|ޞ�֮/g�bU,���'.��xb�*\��w�foKC_{�P�2^�2�O
�۟�ut�~�LKb�nR}�X�R�J twuu�tu{{zۼ�d�LIg��e�6��`\V�4�g$����<}���oϨ��C�ޞ�.��!�mQ$kgCI�fH�>�@WO�Cl�Y����b�s�M�VY6i��u�C�h��<�L��Z4 �	b�9 �+�������[�U�K36���׬}I�i6�v.����J3���SN�Z{���,�L�GL;��Zrƾiط�����s�qߥ�������A�|�ѰfZW����kI��4��)�L�V��N��ΟCL�d�8���	T�i!գ��yqY�r2�ޕ����v�xt�m���e����q��\)V���|�r�4�L9�.kf����ڔq!����K��xC�x#ǛRƛᄐ���RNJ-G��X䙄��b��3�"���� *E��ZԐ��E
�����^9�L����)��3o+'�ҵj���1���a
����y�p�.���Ϊ)�ޙ_��F��4Z]�ey��;�
�F��z&�7��i\I�U�zVՋQ�g=����b�A	�AO׌h�l+ц!t��;�n�h����V\�w�
�B�W�-�ʡ4%�ʥ�kQJK�I�b��F+6b��Hlg��Z/�%��Vl��{r.� ���Qq�4��騬��(�]7PY噀1��#8|"y�R
|�I/�чe��JXJ�m�	�X+.;�t1��:QO	�,9��J=�b;z���7�����I�,S��n �3�p_�"���j��*Z�l�P
06Z�7+�
�fu	T-���+f�'�v E�R��RВ��&iTJz�rK�k]wO�'�֜yGR�%J�%��D�y]�	�}���	||�q�9VNb��!�����=X%����/�B�`��7��N⛧i�o���Y@��D���"DÅ�!�yb�:DE�&�	�=8�����L�ᨄ� )y5*l�h"��=
8#�[��z
.�r�Lb��G�k\����>�c(��3���L�g������y{%��J�o&�ێ*��𧁪NϣPH�G��v~���(���B��}��%"�3)��"{��F�T���WA��;7%"�'���˩�X���73*o��J9�q�\y=�ކ�n��]��͌/�Q��Y�\��^�B��4��	��#�J<�6(خ�����#M>Μ�r8�5^t���Jfl�+ma�b�D8��S�Y"
�D��,�_G�'��M��$��ú,���Dr'7��㻓Tx�^������^�労�hMf�K-L�%b9�NlR"ʬ�|@ˀ�~����+���
���PK   }y�X���|�    >   org/apache/maven/wrapper/PathAssembler$LocalDistribution.class�R]kA=w��vۤ��֪� %I����R������mv3M�lv���-��?�?�xg�m�A|�g��ޙ�����+�xpPrQ�QA��8"�E2
އ�2�	�#����Pj�5,������:գ@LE4��D\�$���t*u�A��Y&'a,��~����\�p��4!�CN{J��V4Qip�bi��Fh�Gݨ��/jJ�o�a���M��P�D��&�ԟ�Ch�yB+�/�r>V�M�w:��#����6Z�		W�Z'��{ә��<iޒzi��]�Hݍ㙋�����voX<�v�8h�[�iޔ}	ğ��v���惰�w~�:��p;�X���Z��`����#�s����iO62��x��	؛�J��o7ͫ�m�9��hH�,$��%�
���}�m��-�	���PK   }y�X\�@j#  �  ,   org/apache/maven/wrapper/PathAssembler.class�VYW�F�$�X�$��iK��p�f�l�%8eR��T�+ؒ#�@��������+=�š9m��W�zzzGR��vO�iF�{���e�̟���;�����
xx%T���ᒼ GR�6�_Rf,�����Z�<���j���P��E7�"rF�I*����h�EC�d#2*[�n�T��b0��'L���
ÖAې��Y5���]>ԡ^@��M�3��?�=�6��e�Ru��~N�zU�2�x�Yi��v+21%"�H؊ C���MeX�v�K��v$b����wIh�6�e(i}A�[�ʹm��5ʀl�p�%��c�ap�������6K�;6o���,Ct�����sCE\��-6�ЊP�a.��T�A�tH�`��X�A0SO-�P�FYF�$<�C�}:�f�[|���������9�ÜC��N�÷W��9U�Gp��c�dK:0�3pB�I^���ͤ�S�6�z�'�_�iP���Lھ��U"{n;�g�A	Cf�
7��$~=zB�;EԇQ�0&!�q�Mk&�����Pc�ʒc�ak0Z�EL2���)�'��lZQ-�,��r��\�	�(C������@0Z�
��1	2/�[韑��U�{E?Ł�T�>+a�ˊ�� �P�)uO�OO��ƦF��8�y	)���*��r�����]��E�dytl�L_ϸ����fّPLX�x�)L��R��QC�R��Xbض&3��,5M�jF��b�8C�� �ʪ��B���#�)���Eg3_xF³x��x�k	�˷��\HJ�İY5��ڼ�/jsևE����+e2HYl�tl|,:|�B�<�0�
MK֬I9���s����:�Y?�j�p6W�q�6,��;vR6T��.z��J�
����v=h,݇ȑ��@)���o�ၘY,F��8�^<,�	w���'�=�&I����m�╱�r��S��5[ٓЩ�0j�1K���3n�m�	#E�q�4���1���F�Y�o���4bMm�T(G���@��H��C���x��W���F��F]dh.�݀��&}Ehd4V�r`?٢oѻ�^܉��-9x��ȕ�s��I��q�P�kuD	t�zтj춑����g56:y��.�_�!�x1^Am��Bm94���9������尓�ݫW��k����^����?m�8D��n��TSeSU]�y�D�C����Hgf3]C8@+щCt���yg鋻1�̻q���_�q|H1� �a|D3���H"��R7�����'�T�$]�\����˃j�?>����)O{�z��� 'k'l6!G+�&`[f��� �Z\��T�3ֵw�F�$R��70�f�I~OA��<��O�I�C)I�<ف�b�m�)�پ�?�^��s��i�ހ���h0��f.P���u�����[�I�Θ
~�u��q�/@��=)��*����ћ�o
-~�zC�?�*œK����|�����������܀��[��ũ��2���'W�4�<��^+x1�������.
Ք�:�Q]İ�z�z��=q��J�������aT�"�G��PK   }y�XR(��  c  6   org/apache/maven/wrapper/SystemPropertiesHandler.class�ViWW~.$�Q4�-�$,q��*�J�Ģ�vH��H2'���n�M�jw>�� ��S{�������ԾwBHb��_��y���˽����g [0�B*%�d��`XzB9�������a��ѣj������3*�ɰ�J�jܷ_I�����RͰ��i��;�ƹ/�j�IS�5j�DB���X�P_,��>)�55̣�bæ�@�ܐb��G�s���Pϰ<ᐡ'�a�<�B���$��$��A����*�%��גi3d\I0�[T0yc~O�E�;~F��5���ו��VV�3�B3�J�GF��N}){8����u9+}#1C�T��܅V�+��W�DB�d��/p\��yn/t7�w:̭CI�dX�WNk���|Ê<��T��޴�pÅ.T:�����0t�y2Ƶf��4'2�,��)���؊mT�J�p��;#���\��/L�&�E3[����у�NS�2���JUH������-�m0��WF/ \3���~[�sa�/���؇%�я�(�E\�UMn(�n04�����@ �21�PW*� ŔT��6�>?,4�e�0BE�Y���(a4W'�=��}�(Uxc�8��t#�S�J8�}	��|��H��M�L��w�~E��9T�32�7dp���Q~Zt @��c2��pQn�Sb�Qf����q<W���t��bRl�vsa{c��'�\s����@V��ĠJ8!cB�[�f g�v\�"B:!C�N�5�t�����la��!!%�D�h8����ZԌYI�(L�8-Pp
�J<ͫ���𔌧�LQ�P�H�	Î2�q�n�'9�ŕZ��Ѯ�.�G�����!�Ik�ߡuˤ����#�VK�Ӊqn��Ρ��a%>����'�̘J7Ȧ���4�TO`��eXWv�FL�E�i�Q�Pv�30^�ˏS�UN�)q�l�|���wї��h�]^���rSVԖ�ʕ,�������/E��J���/^�b�l��Z��sǙ:Oz5!S	O�����aWtz����3mP���EG��	���K���6�<����T_��� �k7z�
�˄�݂ki�V�;�n\1FiWI{z���e����h�{g�.Ӧ���a[�*}� ^��V��IR�\Q�ڀ?� �͠*��	������~iuD\> �W�"�љ���_�B���h��O}�Gmn{�ݶk�hΡcl]daC��6{3؞Aw�v]��`�,����8�p��<��vۮ��6�G2x<�,���Y2m�e�v�$ϣ�����,�YLU���͋���h�6z��`7]�����0�t`O��m��y�@��t�V��H���7���.�бg�|���Ľ���\����0Y[O��"�J�j��x�v���=J����>��vRTP4>>�G�:�>�'��5��O	�
�#z#�Y��H>+�9I}A� \7� ቛ�J�I8O[	u�1	�7)��B*�,��OT��m��*	��1�y��*��������**��g���p�U��[G*�X���;Z]D=H����PK   }y�X��   a  '   org/apache/maven/wrapper/Verifier.class�P�N�@}.%W
�&F6�$,�*�UAY�#rӫ�%:�~���.:�0X����������a��6�I��L�N?yS��u��N,{�:�=3�ѹq���8�N�O�ƽ������?$l��8x�.�u^��`\HL>��J���%D���0���:[��Yߘ0����� !4/�S.�o�M�Β��V\�}ʗb(���|��F����UD��T54@�&���.�%���mPK   }y�X�W!  �
  3   org/apache/maven/wrapper/WrapperConfiguration.class���V�F��!��U �s	9PJ�ӠNM	�����>P�&� ��eɰ�3���*+Y�����G�-���G{f��g������ X��at�N�
���0�_p���5{�V?t�_��3܉͖B�axj�NT~�Ou��_�zY���zM���M�<6N�5��ك0�ĎVJf*{�Z.�ܭh�=m?_)fr���0��OA?Z�W�������L,ݴ���du��TQ�0�`#���Uչɠ��7�aY�r/BS0�O)�W/��]4i߿0�r�1LJ����e�Y-!��+x �����uiV-~�#�N[!s�9�l[?;��|���U*��vv')r�`�I�#�&惺�{��:C��~z1�xO|��Sm��L��U*�0�Hni�t��H����b!��TrZa�a�+a�zlTu�yU`�
�Z�ĪX|�`A0�~5���[R�,�܍�W
��kҽ9	������r˽P�&27{�O����|��'��m�d��p�Z�6i:%^��\����:7�#Z�O�������Z��Z�5C��d�sj�j�]�U�ɰ���p�Z��m�	�t�ʳ��wsӐ���ݚD�x��V���V�H��GT��zl(&�{�l˜�7L�Z�?�H����n'��Gc2o�>L��^��a7ފ��_� RS/��m��y�^;Է�㲣��xD-�A�o�ޕu<�7du���o��	����~?�G��H��隹�o/���kG(����uE�ƕ�;�x��=>y��5F�щ���xt���?�Ɯk�{��n���O �u�!0�{�3��x]��۲I��W
	$id��w>�J�X�����I������F�6R~�:y���)��.Q̲�a���3�/��(��d4�\
�R2Y���6 �>H;��� �$��-�'�X	�$)f���%Q8�W�� HJ
���d:�C1�6 3n�3P���A^IAJؓ��A�Sj"n�(��T�� Ⱦd_��|�5żi��+R�*��R��,At�9n��+�T�� �!y-Wd5rF1f�5_�7nL�PK   }y�X��e��    .   org/apache/maven/wrapper/WrapperExecutor.class�X`��~[���e�8�0D��BH�;�DX�����g�,	�d'@�-�{7-]��J-��(�@K-�t����~��$K� ���o�����#�}�^ ��U
��x��2F��-Ƥ�h�J���n��w�eX	>��a�{;;��#Y;��c��ɥ[�Q�7Z�J'��-33~�j�]!x�1i�$�d˘7[z{���63���I�QgC�H���촕om�ΥiհP�(g!k[�ނ� `��%*��h%,���p�` �8J��Õ�F+�km԰Lay)A�J��I�V2�
�a��cp�B]龭|djI�c��������]��	hmة�x��g]ӗM�քٱo�t��bN�p���8Ia�!���g}}==���|�Տ5
M^�C����5'eքF�҂�)&��А��hӳ�Rs� �&�:֢�d�$�1�LO����\5�~W�\D>�wpN���S���>|�5�ΰ�P�oI&Ƭ�l�p��k�5g�8gQ�gY�:�F��d����T�zXai唓���h�f����%�����G��]G�d�<'J���tl�|_O��5R��ulG�*��f�H��I[����@�b ���3햁��V�G���*,ʘv1�p��A?��e�h�f#c
�~d���r�u��j\�c;��3f���~X)e��X2�.3W?���W��c7F������pE�$4u���~;�*�X.ձ�9�!t̒j�Qh�`�	��tBG����J/�JĴ�~�����| mY�#@�vZ����}��@�0���E�y^���@�>?�*�=z���5��� ���N��N��Z��u\#r���V��:jaħ�����vo-ހ7jx��7�-��Y?nN&�������u�a�9�t�`���:ށw�9k(ch+"�N���:ރ���V�!:�S��>���i{r*!�+d7����-)��W5|(�8��¡��G�[����޲94U���	��
�~T��&��#�U��tr��M���8n��	��-�ȿ?c��$��LL�k8�_^��q� Ԃ�i�:QM��܎;4ܩ�.I@�sx�RN��<aJ�}V�ݒe2�K�K�B=�cFH�{c)s��F�����8$�klJ����r��w�u���EE��t|A��:�r�}Q���+�1�hb����!VW����V|�L� ���H;=4b���bI!:�J'�=4i�-�xH����k�:�K8��-͑=��m��G�-���T��=Ԣ��q�8�cI?���Z��'t|O��O2;,)�~���5���q�z�S~���Ñ���x-��pgg��ߎ_����Is�'H�k�V�����:~�ߋ��澞�Ô���E�?Ɏ�r#����:��i?b�~���-9��k�w��CR�ƸJ�G�O�����tB�9Տ����H"=-��-�7���a`g[�gu<��	s�3���T�4X[[_T;9�,�\)�C�(��*UM�*�O��N��Q�4���/mv]�]���x2cj*P�N��Z��j�b#���eS�D2�Hp�Y��j�T碲�7���Q����R�~���fsg"c�$t`���N��~�m]�V�PGkj���Q����YaS{#%��M��j2�%�[�l�|Dˍ�B��
�j����K��.+�!U�S�ں�đx�M�\C{��n��q!���t�tF��kV�w���:7�w�t��{�zz;���
��b�^��f%�A�̋L	��m��"�vv���{�:�WJ�z���U&+[Y^B��6̞`86��j��h��������SUof���m�vf.�,t�\>ڭ4��4q������O�M���$G�E���ى�f�_r\<(��A�I�4x:� ��1�-�,`���2R��o}�Bo �{,6��/E�j&s��Yr�;b21��|#�33�lz�B�^hAAtFS�֬,^��'��+��"��H}E���b��)�]L\(�S�S�/����ab:f��b)�0�aO��g%3(Fz�A�� �9��
�_�xޤvWĘ#\_+*�#�ٵ-9aJ��J��G�1#��vͤ�ĜL�e��V��*�~a �B�V'A��9��S�)_�q�h|�E�K8n,��8R4��㦢������"(Ws~���y��{j�Z��pԆ�e�P=��9���₡i,�F��L���<g�&��O�%Q�9�k���4;MY;���,���Xy�݅P�d�=U7�f�8�S�s$���vD��s}؎cx�?��=͵G�.��N�k	�]�� ��p��'�w�e�Mw��1r76E\Ŷ�c��lm�,W�љC���@^9Ħ�#�]��%����9�#2��&������C3���w箘C�Jw������׹�k���9��S3x[�rg�ξ�f۝��]Fğ�Ӹ�;��x?�=O^�e|�O�l�"l�Q�����4��W��}��g�{Џ+1��� ���؁[q!�g;�.&��w�O�����Fr�!�o�z�A׫3ԙ�O:�RΗ�b����z���5��ט�J�W��^�I�*�k豏���nG�RwCAX�:Kҟ��id����i�f�[��>�0Vұ��3�s�9�~�h~>�{����M9��>\}2}~9�G8�Hߘ�7������X�T����Fs�qy���9��~��W��݄`���z_����7�<���������!�>�-��fwaO��-�n��9Fk�Y�Rz`/��q��ŀ&��'�)ZC���Y�L�k
WDn�`:\OncL�a������V����M�lΉg<���a�M��pj��j�?��"�b�j��̂s���6�ߖΐ9��������1�,�WU�BWp������� �K`O�����4��߾�����A�{�Y�������
�B-R�Z���ķ���k�z-ׯ��נ�;��Z����v�)�x�*_�׮SZ�{�d^Rk������b��'�Y�i�M��/�/���|q�߁���I�(r�ߋ�R�m}�G_#���7��;���_�T�lr�麈�n��N�*�0��T"U��j��:R��Ku;^��K� վ����@����g@+��~�1��{�&|�����@M�y�?D�rM������U������ȴ:�����$������B���k$.���D�W�)� �~)'�<��A�ꮒ0:J�s^Ty�]�N��`{�'P�?�̼���PA����R����PK   }y�X��   T  ?   org/apache/maven/wrapper/cli/AbstractCommandLineConverter.class�UaO�P=�ut�!���QPa�HP��$,K�fW�(�ڥ�@�����D�?�x_[�����@���}�{�}���� �c[BB"�(��g9��4��Y�c���]���i��6CH��DD�m��jeM?1ԒvfX깣�ˆ��ESݵK%�:Θ�q�9Ñ "$aQCQ��0�fG��|�u4�m�ڵ�3�q�a@��/f��kr�71�x���(0��<˰'g�;�'-_4R����qE�w�0�qL0��/���"[q��s�E1����q�oU\��I��|K�4W2Ð뙿���Y��n�o8@a�>&��<}P-�'��f�غV�i��߃����T�:�L�a�&�E�����Mg��mo��è�)P/c�6�M�Z�u�ID�tC��uL����Ս�k�VE��]w��l�)TK�����]<2��zw1ٵ�89wg��Sɰ��M��QWSN��ҩ.�6���^���}Maُ��٭V���T�C�2�ґ]utc��?�i��9�0G�<����a��i����Pd<�\�}�E������xJϨ_�gxNq�Ff�"]Fȴ��]PQ��	��>(�$%�T�Hc�u�{^�"��f<�Yk@�Wq(�0���8�8E�j�^���eZU�Ҫ/<a��C�|��,)5�j��ljv���Ku�����mKs3ߺǷ�}�(P�F(InD�����;�@].���B���U�'��%^[#�'!O��\��I,�įM��j7�z(�oџ�J!��7PK   }y�Xm�v��  -  I   org/apache/maven/wrapper/cli/AbstractPropertiesCommandLineConverter.class�V[WU�N3a�0���Jm��D���Z����p�� m����83����껯��Z]�����A��}&�-��M�b�9{����߾����0��%��O� Ï&��Y�+e%���Kʦ�ǷL�\V�x��ŧV,�Tr��Q*)z>��괡o����M���ٷ|�HVD��}]�T�EE/�3���	ZD�2Zq�a�D�L�D��Z��v��=;�e[3t��H��x�[o���Mw{ͨ��ռ��pADHF'���x тbZ��!~��I�T_���+"zd\ī���˄��XSf�RRu�b����d�r�.6��ʙ�3�P_�qWΓ��f��"�)ގ����wkmV)K�OB"�<n��r��?`����z��16���!oa�a�,J���R����n>;n�5�N�P����q׉�ïU�b����N�\9i� Q:)�&�l��-"�qNݶ��~�O�#c
������u5g'�a`7%��#�]�%E��W��WB��Np�-HcVĜ�y,0��C�0�r�L^{-z%`q<���>�kUx!㜟T�a����9��;$,�x��-����-[�s�M�('��ޏe,�"#Wk_�S�X�Nm�
̴�'m|�\�������"I�i#�����+�5�,y4���b2�L��j��I�G����<�,�ϲ�H�N{�4G�J%���+��Z}�&�!�Jn%xbD"K��S����Z�S�A�)J�ڇ�:�7��^$x��s��p�wʻ�;���[�m�	W���Ω5�"v��8���-�;���?��	�8kl�g�wL3��Ѭj�y:��T*��y�2lT|�gr�;(e���S�h<�ѓ�� �%\�?�f0~���cZ�i�n��3��ir
=�aV�)�6 �< �B����Gڣb�UZ��>����!.��T�Km�*���*^����R���!��
�p��P}��w�ִ�8���V��
��i�wv��a9��O�[z�7���*r����s�ٳA2��d���͡	X�r�U��x�9F�h����4y���	�[NW����O��e�9��a�[����W�~������}��1�Np�]���>�GA�F�N2E�%���ԣ�iQQ�Y�uk�{�,��:�-ÄE~�{�rI�$�4A�"&��W ����?�Ֆ�a�%��"��'_�������>E6?w��GR��^ ]ţ�4H�G�����KY������K|�O�PK   }y�X�:�dP  g  ?   org/apache/maven/wrapper/cli/CommandLineArgumentException.class��OKAƟI�M�,K�(�[i4�.a!��!�������2��}�NB�>@*�Y���C30�����3������%l g#_F���2�3���T&"��.�J�ZH�t	j�η��(!���Р6�H�Z�X�&*����u��-��Py�E̝p�)�t�XqE]_�^L�!���Ҁ�de�|/s��I?�����AT��e��)a�e0�LDL�q�ݱC`<�����uO����X'kk�LPꇩr��05��ua h¼�Y ��Y�^W�&n��s��,��υ�X+O����B�
v3��=T5ð�K�=rzv�}>����E��XȾ`�f�}d#ֲ��'PK   }y�Xlk�I  �  7   org/apache/maven/wrapper/cli/CommandLineConverter.class�R�J�0>�������7�.4�
���L��в��;֌6-YW}6/| J�R���z��|�~Nr>��? �-(8KdHYʂ�1�Q�W��%"N�I31p��n"r�J�C�31�������m�~P֏�i��9���[�i�1��F�z�872�(�)�@��`�X"�cS��L��$��q���r�����8]�Ly~���g�OL�p0ӯ��rym�[���U\--<�����g��;�GfF@+J��Ss��[�:ƹ��G���4�'V� ��|5��B	,u*��*�ծkY7 �4 [i�m������=����7PK   }y�X��I�U  �  4   org/apache/maven/wrapper/cli/CommandLineOption.class�VIsG�ڒ=�4^P�؃	K�`dC����,�����IFR#��(�K�?�%��KI*6E�J�r�?����T*���-���p����{�����?���W ��u=�)��E���XK�YL.�6y�a��Mݙf�Ec�
�\���ɛZe#˝ ����`@� �NYv1����O����L>��r��ɼ�'�V�����n�Ų�[&�b��
�P���S�؅���T�`7�`�䲥�K����L�"mh�JJPڣb��R5�X-q�YzV�!��U�����m-gp��.��e3D�1�9)O�0�#
>Pq�vP�T(�����:n�ք�	"J�MW<����zJA��Y���b'1���$���D�%cw�.����N�\��^qDvΩ��y�`����jB�dZ=������Vu��� .�ʟR1%�B^�ۺ[Oq9��2�P/�Ƣ�.bm"	�񪊴�x��yM@�h�Ij��5�q�<�,:n���Ŝp�1��h��� 2���;м0:6�[�o���O(I����ȵY��� ����7���ᲊܣ�q�����p��z�Ԝ�M�v�������b��ç�B�Ǜ͝NŖ���V����9[��r�^�5w8c�5cY�uq��>K�����i�3�6�-�Lh����J���w	~ �h�G�ZY�&����D�ȝŝfW�<�'��x����|Jΐ�;'I�@������pI+W�,�h�㼼�c�8H>gCJ��y�jPD��N	Y�&�0��������p����=��Ɛ�[�h�gR�ZU;ϯ�Α�HN	�8B��C_�>���t���ы_?ߧ?z����\�r���u�t{�)����;|��z9�,���K�o!�	�[�$^a���W8��hs��7��?#8�
���x�׏���}���@�N���aD�8H�s>�1|F���t������sh��ny"X��JkM�i������`�R�_!�M?�ȋ��]ƕ��0�<p���q�E�W���l��� �I"t�p��o��b�#�	V@b1�:a��q�0&;b��'1���mf�C0���2iGJ<���m���Qڅ����P����:�?%��)3�h�ԄX�qi�֙7�&�1�����m�\I�o�;�4�n��Rw�9���f]�m�T}.�Ij�%�&5�u�-j�N`�Zj�
q���g���%��T�j�UOh\��P*pdS�?��`��#���j��M�en�2��K�d���&�`����p}��~��� k��y�9,����" ��!g%䐀����C�P3​��s�3mo�ͣ.�e�w�?!�)q���,+{�)��}ֱ�gZ����_�Z_�PK   }y�X�#�ر     6   org/apache/maven/wrapper/cli/CommandLineParser$1.class��A��@D�k4�f5�Y��V�†(x�?�OL�t�nu�6��V����^�
�r�?��#E�0q�Pܰދ��$V�yn�J�R�]]��W��-� �{�"!d�X����V����������e8�/���ƅ�k9�]��O���0\Z+~n8	�m<�F'��.��	z��G(:*�� PK   }y�X�@Ƀ  �  I   org/apache/maven/wrapper/cli/CommandLineParser$AfterFirstSubCommand.class�VmSW~N,,�	T���=
$��[���T	��>^�mXMv3�E�#����ؖ�N�u�����I�XR��v��ᾜ��ܳ���~��g g�$-bHP
����u톺���~��M7���g��FC�;e�w7U�r�L���W��U���l��e���e�3S�g��z���N�Va��3�0���]����q���4c�k1!�4#/��*�R���\)��[��[��xF�r���\�3�blg�w�=e�E^�^W��(N`���N���+\�@͍gf:Zu�����׉��%x7FBx�'��o��~��w,�`�O��#���R�o��p��8�Ot�g�{ӯF���b�c�s��d[w��o�L��]닪�A�H����4�˜`��1�ퟱ�Ә�^D���������rG�_<�d)��o�Ҍ��ƶ~���L�G�o����.1�oW�[\_��DA�^w��`�B��R���4�*�: 
vߨ�f�v����
TaM�}�:�J�g�����4ݪ`�����������%�V��y=,�aI��
�X5�w�ӉN�Y�p�U�w�R]�Z.��x�g�d�V+8�W�������+��W��y�qA`��/\��{��\����"=DC���{O�i�N�j�>�8����~����}#��{���OP8�y��A��QY�"i��QK�h�؍I;��Yt�?�9�稝<�0����'0~���q<�_0�1�+&pj+G��Yy�����;��Vn�����̹G��udڠT0.�8!��ī��G/�+�.2����Ʃ	ڹL�0�2�� >�\!W�� "/Ҧ�F�+�+�Xګ/ڐ��:֐_m�(�K�W�6�W�5�aq^I&�I�:�x )�L�X�&��ly)�Sn���η��PK   }y�X�wM��  0  A   org/apache/maven/wrapper/cli/CommandLineParser$AfterOptions.class��kOA�߳-ݲ-�� �Ze[/T��JL*��5�ڵ,��d�\�O~�x!���2�ٮe� Yi����s�y��ə����O �1�AADE4��۩�bU���.�K�p����嚩O����*E�2��0���]�y�ꚶ� ���#L��	�+\a��!�N�T� L�T�l�]���C�owϳ�N�EW�H�)�\���!̄���Z�����~4�qL��8��HqY��&��>�:�U-�66)�}����ㄊ�$Nb�0N.�KT*�7]G�Dm��}��`�D�N�~��^�k�E�y!kl�-�eQ+	ǔc�u�L�ٻawL�!]��6��[ D�S�b�^�mkbi�cn���ޖZ�t���L�֬m�U0��&��9#tU��l#(�9����ڼ�攍'�̒��9c2,��S�2��h4����ay��a���DK�$W���	�����.��6r\6�|�pj��ɽ��uS<���m"C�P3ٯHn�P�i~v#�7��-ΰ��9gq�zK��yz�[9K�|l�䂠w��b(���c�|�3��Ց����l��Ra�=@�5���)({���p�
F|�CϚ�f��/�C�W.hqZ	H�Zd�IN�8���G򚖊2�m����
c-N/l���UO�5�s:��r� �_Q��������&����PK   }y�X1�GX6  �  J   org/apache/maven/wrapper/cli/CommandLineParser$BeforeFirstSubCommand.class�VmWE~nXXM1`Q��@$���{��i�P߆e�&���R�G�࿨G=T9j����?C�wv�J�l��fg�ܹw�s������� Ɛ#���Z�l'�{�ܕ��x(�ā#����0�ļ](kg%g����='�َ\�9E7��]�'�M欜;M0�V�YL���)픭J�o6p�f��n�؝$$���P@4b����lk�@8%I���q����T�urg�UZZ�����H䅕M�ө
��ކ�0�h��f�M\"3AӔ�b��$�>8��~�n.�X{�F���u�c���N�j��%\-Gc}��4���E::�C���2���CW��mK�U�̀I�c=����N����#Fh4���#�O�0z0��5H����ʻ���⬎a��g��~AZ��#Sz��c�p��sg	��<�vڞBgoO�wH�(!�2�¸��:�7p�'�}��
�� �����{6sKZa!�4Ih�`EA0���U�XaLaF9�%�����o��/�W��I���>����~a[:��v�%-+�)��ɩqI��׊��W�W7�l\��Z� ]��&�p\A��^����Z�_��ɪ�OY�3b��c�e| L��͗9�(��A�������^�TǶУ�j�U3B	n�_����L&h�)�!�=�ӲeIg>/�Eɬj}E��V������%�%�^	�^�6��-NT��E=1j�|S�๙�=��S2$�u7�"�HD�ܫa	������C��߈�����;�0�m���1��54�u,����X�X��,����bG�#6�̭r���% �М��!"k��?�?�=���<��k�0싯|��#�d�<썱~���2-I��dj�O0v��ߎ0�Q+۵�<�bT��b��	�y
4�.�� M#E3X�YlЂј��I|�[�u�[�^���*��n���h�؋=�'�l�q9�q��R�묫�߇��!�S�i�s�U^8֑��׻큪zw��%�S��g���CqI���Dw�'+�Ϲ��B���s-�W<���ۚz��0Y������PK   }y�X�w5fr  e  T   org/apache/maven/wrapper/cli/CommandLineParser$CaseInsensitiveStringComparator.class�S[OA�f۲�le-E���lA�1%$��I�rIJx�Clg��-�W������g�1��n�P�41��9���?�|��s����v���N�;�s�Xx=~*��^�~_h��J��z\��R�=�C���<
�H��V���PX�k�abS*m1����&M�,�{�O������?�a
�������b2��1��haJ3��WCk��A��`��y�묃9�3؉�a���0m�f���Hv��Y��A����dv�C���a�,�f������c::�!�^���F��~$�n
�P�poPmyt���V��wۼ�twi���F-%�t�Et��;�����eq\R�����ʰ1~.��%;�G���?u~ea����"�\+h_��f�#��L��PJ�z���mxk�Ͱ�MڠU���}O��=�4���v�nIF2S��)�߸aeq�tg�{X$��+I�5E�I*���m-~Df����YS1R�DXS���<k:F-3T���R\���Yԃ��xD8������7T����+�H�˕o�Z�s������'���Z�s\��i��5I+�~ �À��3XA��)���2*�\ų��E�5���M�;�PK   }y�X���  o  K   org/apache/maven/wrapper/cli/CommandLineParser$KnownOptionParserState.class�X[x��ǖ��2	F�MlJ"Z���E���qc;�N�:В��؛Ȼ���N���{��@k��Ih!@�X�q��B(�Jo�ח���/���k{��J�%��_��ٳ�3��s�����^|@;���
e(�mZ�15��G�ب:��qKM�4+O걝��j$ztCۯZ�f��5�qc_��M���wTG(߮��)���)NeG���<�������7�
�]�y��7�)+��A����ݮkh-�A!H�R��Cv��-K�ׂhm)6Hu�h5
��<������'c]����m'�
T2�5��޲�%��ZZ���Lk�@Eϥ�<��G�J\�Hs^Pn^'X�:����yOq1^���Z>(�!�	��_�|EU�$bU7HlD�@��`�r��I�hD���a�٣�]�pzT37��l{��sw�k�� �n�G4'iH��cd�{c$ajv�0����"�Q}-
�(�n���1�7tD�?ڱE�V���߳/�۴FU���'�k�=�
;
G�Ќ���U�hi`7Kt�#d0��Έ�� �( ]FDM9�s����v$e�czBKDa$~����N�R�[�V�F1YHu��&y��+9����(�
�L���JjL����呤}
�I�g����0�+�4�sܲy���g}�n�fw)����"q��MA�L�zqp�#�	��+TA�+�vs����>s)$�N|R��$��a��e�0*9j<��vݦ�V��ۗ�,�K�!�$$4�}%v�4���,SN,�nŖ�+P� �?�9"�sNl���(�
F%��K�J�#��W��!�������yP�K��t��)ws��~g�;G);2�YZ�eLb����Y�cZ�@u�������J|����P⎨v�v��_��"�D�3�C`M}Ca�b�~E�\XWRԳ�c'�#q/��FNإ�,-�2�e�,^�0G�	|C�7%��oQ�<f�K�-��}��!�fl?�qZ����&쎴�LhV��2��h~}@�A��v6ePuo^l�)��v��IV�P����pٙA�X`m�$�I��'*5����)G%�����
�8�Q�\d�ċ�%����Y�*�kz�gg��
��8��nZ��DA4�>�����E}��͏R�_6T'm�����^h���v�	}��K�i�u(I=�3�&TK�w�3�z�_�0�B���^�1ԩ���������]P͉��P�HQ�h�������Z';i���{��vʭ{Y�o]Z����w��5J��r���6n6s������c�ʹ�nՙ�cZ�7Ān�Ь�Iնy���ܵ^�J|��`��?���XԴ},zD+��%�r���HR�ou��e�.U�.�x��<SQ�wnwf	��A�9z{�$M��)(Q1���9TN#<��h�j��S�.Z���њ�)lx����:(�x����J<�Z�֋S� N�Q��s�,e�����w�+1�RWb�Wb�e�"��&���@OQ��O V�͠�!\;���p,�6�F_@M�6Oc�+�l�~���3�U���Q���hk.���'��'��~��4nϠS<�n��jAn�}���%9����H~{}ć�O�sټ.��Ir0Y����X5����s��f?�T�Y��Ϲ����7<��K�o���<(�[;�]9����}E��]�|q/����.�w�A��9��3�����YU{�i�-�#�p8p*�T60�⁙EZ��i��l	�8������������������s������28��2�a�G��I��'��{?}a�|4�3��9~��� �WQ).�N�F̾Hxw�71.��I�6&���Έ?�u�g�CϿ����o�����f��I��уY����Nb�/�O�5(o|{}+��7jo\K ��T���pآ�M�5���i�g���kq�7.GM�W�W:�g�į�,������q�����U�Z\p-��߹��	�NU�Nz^D-��&�F��������=��vo��PK   }y�XmKs�  �  J   org/apache/maven/wrapper/cli/CommandLineParser$MissingOptionArgState.class��[OQ��SJ�[( �xD�Y�"Z��\��	�eSִg����/���A��?��8g���a�4ٝ9��������O �&BGa��,����(ozM�RߵE�n�z�j��V�&�fɔƪ��}e:�)++u״�S���
� D��t���R@b�p<�lv=��Q$T�IB1 �F�\�$Wg�R����Y^��gg�M�ߣ�}������L��zUȊ���,�l�m��⢆K�Lxr�섄%9�^5�3��m�x+1\�p�)K����զ�6������a�^s��ܮ�%��S�,����&�֭��k��ZlTy��d�Eu]ئ��Ű�e:���9��0Ξ����1�;�]Q�O5JHZrْG�i�؏-�Mޛɮv�����"��&�c�0�g�fm�ec�TR��W��R�|U8��D1M��o�?&^O�ەl�(&�v�	�1��|�j���ռ2��s�B�N���\�;�}vC���.t�N�� A.Fym��[�x����χ}h$�?@�K�����Z8�&'�����y����rߠ�B~� �_O�>x��F`��q��b+�v�>��_`\����'�[j�7��f�y�����Q\㛿����}�pZ#��K�O�YPц`����X-�
6�	�,�;�&��S�����.�>y�0V���~� ��PK   }y�XV�{�  �  K   org/apache/maven/wrapper/cli/CommandLineParser$OptionAwareParserState.class�UmOA~��^9��"TŪ i�^)��Ҁ1$�i�߶׵��w���C��/�45���Q�٣`	rD���y晝�����O ��Ћz�5�A�a�v:osc]�-�),}���pt�i�%���V�lZb�;�p��ڞi[[��h*�C�[7�t�!_汨"�^��>hs:żi���ى�a�������Yq$���SNd���w9%V!�k8����ap���or�ɭ�^��j3/T$qA�E�0Đ��������U��ǠTU�pE�U�0|ʡ1��0��g��3AL4\g(ܗ���p�L��*��Y�Ҳ^��$�V����`(���n��*Ƒ�����&d㿧R��Kv�j4.+��p��Z�4�e���*wL)w�a�Wett�*2$���n�I�[��M�-��&h�����4dD��5�Z�����G����0F��2owR�V��K���V�$>��Ĳ�Sjr��)y\���P������%��]��a�Q�sB�H$�C����'�2ERY^H�Od'�������6b���7�{V��(T�F��!G�$�ht�$1B��qo��C��4Gf�(����s��G���r�w�]���Y����"rV!�1r5H���#���5)I�����:��'��Ĕ�XQ�w��q�;4V{�P.C��}�� Y?��a����*����R{L�!�����C PK   }y�X��`�  �  E   org/apache/maven/wrapper/cli/CommandLineParser$OptionComparator.class�TmOA~��zP

UPD����
"�VM�T0�!�oKٔ#�]swE�?�/~#��?�8{WAڪ��fvfw�gf����� ,�E
B*�1Dp�aӲ+����j�X��������UC�[�7��)�p���n�5,��unsײn���n0�R�(�����#~̵*7+����(�Q� ��7�>�3,�4��!Z��;�t��p��V��b���eSrmì\�E	���R��ά.iHⶊ;1�`�a�
jުV�?�P͠ĶR�N~���6���o�R�������>�N@�y��1\�X�33���=�a�j��	��T���nId�=��3�oN�_)=��tAE�a���u@��偝Fm_�o�~�,�E�̫{�6�i��m;hC�>�h�<[�!4���9BD���h�x�}���4��ݒ.P��u@�yM�¦T���\���]2*&w�3��	AK=ւR�A��%�a��+CV(�fAb�LS��*w��0~�{���ɗ�<>T<��%�a��z:]���$�N�ViZi$����Ϥ(���tCa�%=�o��I2� ��?�e�OgO=G�;��_���\�i5|�1��	����e�>�H!��1�D�����ͨR[��J-C��&�E��N�OHǷ	�"�$�e�н蝙�@�}��:�K���`K�u+�Y.���}���
I�I�LW��p	<B����3o�
V=�c��W�ulb�"�c��<�a��<FPK   }y�X7A�ϳ  !  F   org/apache/maven/wrapper/cli/CommandLineParser$OptionParserState.class�SMO�@���$i���~іC�Dm*�T�RH�r�1+��^[��?�kz��C ?��['Bpq�e�=͛��^������(�\���*��Ą�HEp*�X�K��0"M��H��$��>(-��ɤY?L�J�Za%��Iiew	�NwTG��? ��Ĺ�#�C�p|&K���	,8��I<��G�Y$��F�(WϚ�=U��྿�#4���ya�MH��t���t�'��&��|������Ck�{��o��~��N�8_˓i$]��;Ewl��#B�b��]|����	��.�}K��R 4�i-M?Y&�*^6��",޺�*��?S�����͢	xk�zjp_�� p&\���8���?��0���I�h0oLx�&��+@+�E,���s\v��S�g�K3s�ۼ=L�s�O�\��g��(3��^�_3�q�V�PK   }y�XO��4�    A   org/apache/maven/wrapper/cli/CommandLineParser$OptionString.class�T�NA�N/l[���\TT�v�l[�ZP#��A�b�߰ݔ�v��]P�'�/$�&>�E<��"���!��˙���sf��ɏ_ �4��
b*��!�����0�M�.�L[��F�tu�f��N�.�ʊe������Z÷{�w-�J�q�)!��#��^vUo.�S�(�"��[�l�L�ɶol��6RPѧ�_��[ֶvL�'D���%HT��	�|>�a�(��bc��-�)��ｷ�m�PQ�
&#�.<۵j�Ma}���b78B�4�B�w"k3�4eIqK��		�i�̶C�	�(/�S�IH��[^�&����I�-;�d�Vw�[��Vl�LY ���Zrc����V.pʄ�u__�FH��_E��i��W�sA6Eٔ��Y��0Lϛ*.��M�B�w(�ug�5��L�pü�ԗ�m��5�y��`�0ߝ�8�E�B�ۄ��J��!����2�m�>�6�� ��lS��h���f���At.�� �"F1$)�~�A��M��H��`$Ex��9t�s`Q��t����b�'�7�qUN����;��om-�>����h�)�C�@���u�W�~��kA�
�t��Ƹv���itM��3L�&S��b^c�1ɬ��C98�I ך[N���+�$D�p�t�p�MG�ߑ���PG� h�QP�e,a���t�PK   }y�XX����  �  K   org/apache/maven/wrapper/cli/CommandLineParser$OptionStringComparator.class�T�OA�����q����ł-E��SB$��&LjH�m)����k��O��/�hb�տ�g�� M�fvf�7��۟��|��u
"TQ�0<w����\V�	�z��FCxV�f[y�^��A�v�+���m���v*�mp���гa;v��I��t��}�!?�V�;kw�P��04�`���IH5�T�j�T�p�a������o���^�Ò��j��%x6'����8�V��l�x�M�Ӓ.�	�5L0���f`׬˽T���ʬ;����^��Y��n����g�}ۨU�gx�-��'�#Fn���|�_��hR~���co�n�+��n�7��]iK��:-�]�mT����E <b�|XdX�~F}%���)'�q[�Z���w�������-��;�-�CF�q���q����Bwe�m�����5��n��E���`H�Ӡ�N/����"�HF�Ρ}&EA���e��� L�>I&�������ʜAW?BS?A�l�_ޭ��c��	��i�D�ǈ��_a�a��P��)�D�ē�(�(�8��f�q������dX�J+i� �=�ð�-<"M�'g�xbNc�4��7�c<c�`��W7��N�NH:�!��|G�<�3�=�GOb�Ҵ�	/�)�5�n�S�J������[t��C��̇�O�J��a��,LP�,5#�Uy���PK   }y�X����	  2  @   org/apache/maven/wrapper/cli/CommandLineParser$ParserState.class�SMo�@}�8upS��P(P ih\ B�@B��JA9p�$��U���n��ąH��(Ĭ�V���Z�gf�͛7��?��5)�mX9d�Bx�+F�;��/Τr?k1I�v���|_�^�S�X�P�Ĵ"I�ʁ���!]��d�B�D�	w(T�=��n��*c�jUk�jc=�<
S�V�=�'ؾ�XKH�(7g��O����ux�w��(:C^)6�����L<^����d����B}�#9���(�E�zṛ�o���%�j*k���m.�6��ּ��@�>^4�K%P�w�GxY^��Y��&����/�KpZ������9���f�&�>(%uc(�P�6�j���qN4oc����gD(̭��#�/;k�/u����9r����O���I��6����� 6P,�b��l�:g�u���@��M)�)%��&�naĄ�q'&��]^'ܻPW�c~g�mM(��2o4n�_�M�ۚK.N��\���>�ic���D���([*l�l�x>�PK   }y�X�Ć��  g  M   org/apache/maven/wrapper/cli/CommandLineParser$UnknownOptionParserState.class�UISQ�:� !"�"j2,	���R�UR����L��d&5� W��)�<����^�xQ�~3�M�
<��t��M�~�~�y`w���B@EM���M��Hoꉼ���Ķ-
�N�sFb��煙Y6L}U�E�xb>3�m�Q�1,��[s�����p�	�c�OŖH䄙M�9�afg������T9;F�6��Hf�04��9��6V'��X��r��u��6����:k��J*:q�В>�!$eMBu���,!X�"�;��XΩ�E!l��\��u>?�\ᒊ˸Bi,OB��d��8�H�\���uJ.�"��K��R~C����.�������.o�M�HXj4����,�-�;Eq�Ζ��p���m��a;+��� ��=�Y���i�&(����?_ϓ\$�o')�Q9��a�0�83�f��~ߐ<Z�3"�!�K��ۋ9Q,��nW�58����"�����6�Ǫ��n��F�A�o�¯�����u���JnO,�X���y��U�e�Fi�{P��=D��=t��Wy��	���a����3���L� p�w&�;�.}�L�����]��Z����請�z�e\B}e�D�2����_�ߪ��c�e�i�������QO��Q�cmw}�Vb�g�"��v?u�Yt"������Ϫ�"jD���֙A��684| ��+������S�J�H�g��xV� $�r6�1��a�E��$�~wg
�8�i����Na��m	Q~PK   }y�X/���x  �)  4   org/apache/maven/wrapper/cli/CommandLineParser.class�Z{`�Օ��df���B � J�!IHHD@H h�jP�̗d`2g&@,j}�g�֢�>Z����"!�W�V���խ[���v��ڧu��<�w�<2�Bp�����=��s�=�wνɋ�
�l�/Y�6�0�K03n+�:��v����l˷���N;\��׆::��o�?h���;,��ٝa�Ŋ�C��a0�Vn�6[��P�Z�s�J-�6��`�����m	��T���S4��)��/�n�RLGbr���@y�թ%��$����[m_\��1�		MVuE;���d[1�rP�qN01^A��X�����H��d���C����� '�������?Z-��̴^U�U���i� U�5j~2���H��)��y��B5�����NK�p�i�#<8U1�Pt�{P�I�a��r8-!_˨	��n:�eEV�#\hZ������*Nq�"�R�nb6���*ˋR븴�����A�#ɗZU�8Z$a20�g�h.����B�X$�?�y��i���TkC� �f��y�Q��d`wӨݛ���j���N�ޝ)X:�����n����p$�����`9��jg�}�w����nS���hk.V��@��UXM���[Ŧ/ƥy�.>^��s�h�\�aMBJ��h�"��X~�S_6фuGP�M�N�f�'�6�+.4a�YP9L�c�ƨ�uyVw�͏p4Gđ����XU��� ��̃�h5цv��}Q��bId�:U�Ԯ��>�#�~T ����O@��^���߰��ԋL����+JA55х͂�HWs$�Eu�Cݦvj��n\̜��ܞ��?\+b�S��Jl�%.5q�&X�E
F�MQ]���E��3UC7�x�^_8�D9�*W��t�?賷�je���uj�Z���T:�ev�6�`�F|�BCA*��a�b�3������6�VU���}�T+B2�K��$>7��bk���f���h>�NM�|Y8b)܎)�&�0�SeZnK(����
���.��`K�t�>UD��7�b�R���1q/���&m
2��2:�Ҫ���&��%�t�J��M�Ha��6;�Җ�z�ďU�qK��΀��f���Cx�
�`[�]�c�ʛGM<��07l�{/5�c�W�ZNbw�}�����0�V7�Я`䉁�s��I��=�V���6+��g��+g�����ƳJ��n%���������ܐ��XD׳�/�x	/���D��W|N�@���Dp�{��&ۗ��^Qa����o�	����/ˎ˓�K��xփ��ůT��d{>:�L�r��K�K��Ѓ��E��=�n�}�k#�;HF$����X�JU;�&>�HC~��#8�X�J�_&�[%�J��v�%�ז��G
���_�W*ҩ��y����!%�#Wsm�����k�&>��	�5O������ʾYm�b�����fR��uzJ�,�d�25���SD�8n�Fu�nɦۦE�մ���vq���4�)9B�t���pn�ώ��'��-&7m�BI �Q���
u��2�),T**}Ƙ2V���5�i7d��P��V��0���ܐ	D�L!A����Y��qXɮR���j�݆�"��ҧRv�H ��-v,xd�`R,%
�O�L/�G
� Oy��B�����$��4��S����B)"4I�z�5j����F�Q�Cd�?��^k�`0-d��RԵ�n01��Ѭ�[�Z8�l�L�;K=r��2�tSf��3j`�� k0��gG�aۗ���vwq"����k�Mc��}A��lx`WU]�H��lu�ᆮ�f;|n�L��2�b�Xa���:��~�U1܃K��s:q6�p|l���Z7�O&z���d�E��s��0$j����K4�K�x���)A�nS���JT��?! FtyCqM�-%al�33+N�`�u39ۣ�ě�_�3eا�Xwg"�2�3Ļ��ֲ��g]w̼�i�.����a�	��u}ְOڧ�鼈�F�PJ;������h��Fү�T����0!Si���|��aIe��W5�I�8`�U��E^z=�[��ueL�&J^:�@%Tg�T[i�3�����n�Ba� {�@V�I�Lh���f�*ǁ�s����6$��ك�KG�U�����=In��&P����ͺ���w�J�Zu��=��%��E�[N5yL]��ՇL1GJ�"���bG"S�TTN��_1{���r�)95×3��}@�|%07�q�V���?��r�>v��iu�[��~?ㆈ����A;����/Q��`2�#�L��1S��X�2\��>�R�̧/ƨ�kC�%h�j�VĮF�`��o��ʞt
C�s׆�j�_p���)C�3���#�2.�!w������#1a�k�Kd7仂�㹃���LÆ�s����-V�N�R�	C�?�{�Ui��L��)<�O��sFBd��d.�Gã�՟���Wn�ƫk=~�V��g�k!�F��#����ń^���6��� 8�lu2�of�\*��O%�0.�|�R�Kzq
ŝ�ԋ����kqӌ^����Oj`aV�D�k��b2����,�:�c�,$oL�,M�S2cJ{0롤X�Vom�������ETv1�i�Ԑv6� [	�~�~ʥG?�6�c^S�c��C���{Q[ߏ�M%�ҋ�f�a�}��_쐗��Z�3X_�,�:{p��q~�z�w ��&ź�a���u�{йH�u�$҃-�.��_%�6\�	fz�y{p�\W����(Z�kx��P�^O�3�t�f:E�:�qR��:]I�I���( ���މ�~l�F��4YHT�Rf{]}�Nv`��劯���/�+Mi�)//��tg�w{p�"���W�-�n��?؁|�;a.U��j���ܜ����<Z�/e�5��t���HOR��W�Ȟ�?ۃE��HU7>�ӎ��"��-{�aϡ���-U����ԃ2�63�[0>L����l��\�Â�����܈lG��c��Oq^C�A�!����YF`����r��K1�1
/�F\*�q�l��҉+�\)��*�W��ދ��	� ��Fy7ˇ�E����1n����pGVv���H����R��vL�e��o���j̠ffl����X�1YΔ��񸹜yӈf=��9�b� g�Π1^7-�HVp̅y\V�ͩ2*��|��zf�x��4�*��y_V˗���$�ES52?�����sl0p��1��p����oQ�_�P���ޏ,�8%k?\|��8:���5�6Xs��B�ı� 8 "1̻��ߓ�yF�r2�0���M�e!_�1���-⎼֏}
&'�<I z��x��^���,�D}x>k��
��JGY^#1�c0��՛���+]11o*1�B13��Jy�B~�0bRr�9q)k�[�<*�t܍�^w�l��;���[�N������>���P��Tb�#x(�;a���7���ß�}E�\��Կ)��v.IZ���[\;���2��}�x�s��w���,�$�A�>��z�c$��$&7esF��1hQUiܠ1�Kc
�=2b����r�149�Bc���+����r1N��OX����`�j"���D���m؇���S���I�����Qd?��!�\<x����1�f����/I^�-xEnƫ�^�����M{7�x߆�Ҥ��+�P�c�y��/0o?�T����u�|Eӭ�����۳�g�Lo�%�4����jy��ܢ��y����J��r�l�b������|B4q�L��̈iҞ�������c1�2$@
���è��@�!�x�@�ğe��0�~W�Bb�~�H�8��?�g���A��A�	��c����Sm��~�A^���!ޒ|�ovY8�_
�z�zG������peI��O�0-�+T:Oډ��=r��R�9Y�eW�9��g�@�����>��5ME�&���R��
V��x��[,��h����+��b��XE�J/�Hº:["�K���D�ҥ�X,������~�Vy|<1ڐ9 C;�N��)F�Qu+k�
��,��)O�Q_�Tu�\G��{q^i�cV�S��x�0�_
����x�9���^��N�#%I���W9X�����M�����R�k����L�%Q¡Wu�&��=]߳�og�^�������+��#���h䎮�����|���)ng��gI���r]휜�3D������.�X���Й���9�Cp�U�;+t$~�A�0e�M�s�6V�X�)��#�w���V��\�&�e�̹����̗g\�8����+30�1��ܿ*y*��g���Y=R�
��G�����!��Ky2'���_�k4��r~]�|�N	�sc�op���ɒ1����C�i;Kn���|~�σ���]9r�o��6���y;�lce'���}'�w��n>�ud���܍	�PK   }y�X��sP  �  4   org/apache/maven/wrapper/cli/ParsedCommandLine.class�Wiw�~��-��-�`'۲l��M�LL�b"��v�2X�<D)����I�n�o$�7����(�t�֞��~�hz8�Ͻ3�%{�Ѽ��{��y����>��_ ��w1���E�@�ec�H;�>{�9�
4�l����hh�TJ�*�O�I���pZ�kh�юmCE'�6J����^4�L;}�1J%�I����S6s'������Z�)��Xr��]>�<�:���Nv���j���}�tc���б]�%�,��{�3�h�hh�����q�1��V��vy���(�)���N��H�4�QgJ�m�^$4���8����pMǸX`j͖ݢ#���_ga�ߧ��8��WG��VT��<�xT)��:R��j{G�v]�k���QW2�y�z'cF8�!��x?���͵:t6$/+ъ�%zG�^hm`�G�V<�1�t| �GH�R����6��q' �f���0�8XN%�/�b$�I��A��J�5��ܙ�e��;������hL�8���^��΢����mT���q�Z&v��>^�
9Ӊ!�p3��rqN�4^�cYK;'0�x��A���ҋ:�p��v�Վmd`-�V���#�(�z�
�	��)}\����e�h��%ɶ3��mr�pl�k�9Q,�Z�y�Z�og�pw�{^��e,�|$��� �AMYx��-�7
U���:o*��xE`�D��`�7a�Dμ��sC1�(���n�L+�pE�U�����Š�\N��	�ī���x� �ݗ��9�i+on�!�s�#��� �V�1����Y`[jX7�7Q�ܜ$s�D1'??��Te���M�x�8ofǒk��#0-K��8J*�*q��Z���~0�H�]���yr�K�Ԋ��#&~��X.U�8��aC�j�v����!��DP��`Y�����'��rU\����t�Q�*���(��(�mG�o݀yIq���Um+�Ys�,�>�]��)�|�I���M'��;��>o�������77����FY�1
=��=[�_��Z�d{Xl�����t��̛�,IɮzC22��G#��tqy���k\��)�y!��?�5o����h�3��P�, FhA��k�;�{����b;(�oa;�]�o�g�.�L����u<G!)�7M��0ug"����]<%��vG}����b&Ӕ�b�Mݑ�8����VV�6#��s�b^@/W���~���y��c��i$aa Wx��A�J�ϫ�Q��xM�:�/�K�p��|�2?|_��5|�x|��Χ��MJߢ| �U����V{5��yK�0��p�D����<���$�ѷ�������p��n��g��"R��q��:+smW�F�`��\��~�!y��*�w�l�~�!K��&#c^92	�zDJ>����D,?{�M�?��ѺUxle��ۘ�������sG�{�`�2�g(�a����E��I����?"�!�Q�C�s��B���������2�5��u��?@\CV���Ҕy�>�oR��$�	���L�ƅY�5��X7j�ƔB��M�H����:Ɏ7hN�-r�C�->#|���|�{�T�L%I�K���XY�w�F�.��,)�"�#�.��/)�	��À�CY��Z��~�@G�����ƣk��)��b����,OɾbЗCrx2�w�{.^�(2H�݁���M55d�q��%�0Oy��^����S;Q�R�5C�G/ۂo~�������0��kT�Zî�x�7�������ӳ~mui�9UB�}{]YR�\WV���_�p�_�7�E�/���$kRj`]����������U�'�Y��٣�P�3!jw(���]Q�~�?PK   }y�X�v�E�  a  :   org/apache/maven/wrapper/cli/ParsedCommandLineOption.class�TKOQ=��-��Uax�m��(�G������IM�.��N������čLT�$��e���@���E�wN���=������y<������ ��[|��7+���V��tS�s�D�(!����l�P�,������ݐ%�ȸ�^���UQy����Z�ۚ���x��Yj���W�jj��z���r^7�����M��FKk��|[Hhd#��'#�~�P�7�"�1�!a�a��~�0�
7
6����%���2�:J��5�n+U��psGqt��h��`"ߦ,ؖnV��b7FpM¨�1(�κd4�]��jÞ"9Vj-����5ӂkJ�uLSBE���j2�9���ę�fDvJ�nR6/�;{�������Izs�ְw��br�e�٩�^/�/<��Җ�e���z٪mj�k�i��K�(rK���:�1���E �5ě�@"y�C���ox��������a�xA����%�Ҫi&%�O�����-��=Ӆ��*J�D�������&���|���c���6!9��DS`�#H�E�}���g��<�d7w�q���ǵ�qeR��C��c�\�����!�iV<x��b҇_��O�~G1N�@[/AA&0�I$hy��O�����	}�,9>TzQA��RH�&O�=�5�=W�SBD� �9D҇7�M��&�����S����{�bn7f� >"��tR��q��ةR��Eɢcd�/PK   }y�Xx��͊  5  H   org/apache/maven/wrapper/cli/ProjectPropertiesCommandLineConverter.class���N1��
"��n\�&0w^Ÿ"J�q_��P23����te���-�18Q�8�����M_ߞ_ b'���H0�CM*��1�:�|@�{�x�r�@�g��V��%���j2yԮ��j2�C�XDB�2�J��r��C��+�%OWL�s(0�7I;�f[w��I��9���N�S"�N��~թ4¡�����T�f�h���xc|�o}�k�MKV��-R7��X�n�v=Ӻ#z��}İ�Ӈixk!#��R����<�ݦV"�u����4�v���7��oʾ��R������ {X0?Ŏ4��+&.��k23y��	��Lf�lbf$na���؀"VG��'pe��ه)t���D7йit/�L@�����kX��;PK   }y�Xkn�4�  &  G   org/apache/maven/wrapper/cli/SystemPropertiesCommandLineConverter.class��AO�0��nK��Bi�1��2���\&q &�V;�2&e��M��$��(�j��v�؇�p�2M%�I������=�m����� |�Q+(հ�2C_*��1���|N�s�x�r�@8�D+��JIJ�2y4���2��C�LDBb(�;#X�2l&i�)�Ʀ��ÉK�^�v������S�/GWv�z~���4�=�9f��U��p�Yw��%����C=����1�o|�9�F\�,_�%=�y��O>eh�����u����ڝ�w>�N�#�q��oJ��HsЄ�8g/�Xu��r�<�,2��������-�5�Q�>��u�9fff��pvg�X^���0��,@�|�+�����ʏ5����}���YG[��A�����A��FVu� PK   }y�X��c  H
  =   META-INF/maven/org.apache.maven.wrapper/maven-wrapper/pom.xml�VMs�6��WluJfL�9t\�%��jl�c�qs����X 4��HJ��8�N}����ݷowIE�e/�4��jt�� E*3.������u�1��_|߻�)
�	f�0�XJ��4S��3ލ���@GT zRA)	�Ja_ԆEX�KF 	��=��'�o`��2�['J�p�" ��H�K�ò�ۤ� .�P:
�)[%�6��+�T�W�in+Hn{���R}Yw��v���V/8N��Qw5z��s-��4Pk��\�XbI�ʪ�L��um��o]�0���U r9�3��2��æi�hR�a_UxG2N���zޣ(PkP�w���� ��H�D�`X=m?\�)w�H[��x���;�zVT�@1��	L�|'���&��g�sx?<����M��<�^O�ٔN�0�~�L��'��%�u�,w"ȭv����n���CW��%O�$��,G�%͸��P�*�����-���ܸ��ޫz��i�+%�cj�vE�Q'y�^PE�sv~N�9��Z�-�6��p���ӳ����K�s�����R;�L���M�>Y�5��y��D�̰�8vwQ�g��U�nab7eQ�d]M����)�|��B�=��3e����-��Q�p h=��P|\�Q�-����y?�E
Vb|o1��b�F8g��u�xe�~����d�	�E70�T��vW���@b;��	�4wc�w?A��v�A�x-�
iE�Qw�lM��pД+S�sЈ}�v�C�8?��]jzal�o��e�����>+n^M�>Y��;��*~���F���37�MJ�O�e�����3�U�Q����[G%�[��̳��K_a�L?�㵨y�=Y��i~w��mh:�mLk,��o��T�\q����1�:�iѲV鶐m�aE�Q��y���V�g^�� �V�=�1'�̙(6csӓ}����?�=��t�_��rM�ݔ~�l� �tGn^�Ya�@���~���N��-,
C��Y���c��PK   }y�X�\�@   H   D   META-INF/maven/org.apache.maven.wrapper/maven-wrapper/pom.propertiesK,*�LKL.�L��M,K��-/J,(H-�J/�/- ����%$&g������g�����q PK
    }y�X            	          �A    META-INF/PK   }y�X�1Oe�   J             ��'   META-INF/MANIFEST.MFPK
    }y�X                      �A�   org/PK
    }y�X                      �A!  org/apache/PK
    }y�X                      �AJ  org/apache/maven/PK
    }y�X                      �Ay  org/apache/maven/wrapper/PK
    }y�X                      �A�  org/apache/maven/wrapper/cli/PK
    }y�X                      �A�  META-INF/maven/PK
    }y�X            (          �A  META-INF/maven/org.apache.maven.wrapper/PK
    }y�X            6          �A^  META-INF/maven/org.apache.maven.wrapper/maven-wrapper/PK   }y�X�G�q                ���  META-INF/DEPENDENCIESPK   }y�X���m  ^,             ��V  META-INF/LICENSEPK   }y�X��w��   �              ���  META-INF/NOTICEPK   }y�X�۱A�  U  3           ���  org/apache/maven/wrapper/BootstrapMainStarter.classPK   }y�X܇�H  C  2           ���  org/apache/maven/wrapper/DefaultDownloader$1.classPK   }y�X�4'0  �  S           ��s  org/apache/maven/wrapper/DefaultDownloader$SystemPropertiesProxyAuthenticator.classPK   }y�X�3�    0           ��   org/apache/maven/wrapper/DefaultDownloader.classPK   }y�X�y]�   �   )           �� .  org/apache/maven/wrapper/Downloader.classPK   }y�XK>8ڤ  {
  4           ��/  org/apache/maven/wrapper/HashAlgorithmVerifier.classPK   }y�XXW1�  *  *           ��5  org/apache/maven/wrapper/Installer$1.classPK   }y�X[/A�  �#  (           ��r8  org/apache/maven/wrapper/Installer.classPK   }y�X;n4GR  %  %           ���I  org/apache/maven/wrapper/Logger.classPK   }y�Xb`3�N  ,  /           ��L  org/apache/maven/wrapper/MavenWrapperMain.classPK   }y�X���|�    >           ���X  org/apache/maven/wrapper/PathAssembler$LocalDistribution.classPK   }y�X\�@j#  �  ,           ���Z  org/apache/maven/wrapper/PathAssembler.classPK   }y�XR(��  c  6           ��#a  org/apache/maven/wrapper/SystemPropertiesHandler.classPK   }y�X��   a  '           ���g  org/apache/maven/wrapper/Verifier.classPK   }y�X�W!  �
  3           ���h  org/apache/maven/wrapper/WrapperConfiguration.classPK   }y�X��e��    .           ��5m  org/apache/maven/wrapper/WrapperExecutor.classPK   }y�X��   T  ?           ��Rz  org/apache/maven/wrapper/cli/AbstractCommandLineConverter.classPK   }y�Xm�v��  -  I           ���}  org/apache/maven/wrapper/cli/AbstractPropertiesCommandLineConverter.classPK   }y�X�:�dP  g  ?           ���  org/apache/maven/wrapper/cli/CommandLineArgumentException.classPK   }y�Xlk�I  �  7           ����  org/apache/maven/wrapper/cli/CommandLineConverter.classPK   }y�X��I�U  �  4           ��=�  org/apache/maven/wrapper/cli/CommandLineOption.classPK   }y�X�#�ر     6           ���  org/apache/maven/wrapper/cli/CommandLineParser$1.classPK   }y�X�@Ƀ  �  I           ���  org/apache/maven/wrapper/cli/CommandLineParser$AfterFirstSubCommand.classPK   }y�X�wM��  0  A           ��Ӑ  org/apache/maven/wrapper/cli/CommandLineParser$AfterOptions.classPK   }y�X1�GX6  �  J           ���  org/apache/maven/wrapper/cli/CommandLineParser$BeforeFirstSubCommand.classPK   }y�X�w5fr  e  T           ����  org/apache/maven/wrapper/cli/CommandLineParser$CaseInsensitiveStringComparator.classPK   }y�X���  o  K           ��s�  org/apache/maven/wrapper/cli/CommandLineParser$KnownOptionParserState.classPK   }y�XmKs�  �  J           ��̣  org/apache/maven/wrapper/cli/CommandLineParser$MissingOptionArgState.classPK   }y�XV�{�  �  K           ���  org/apache/maven/wrapper/cli/CommandLineParser$OptionAwareParserState.classPK   }y�X��`�  �  E           ��=�  org/apache/maven/wrapper/cli/CommandLineParser$OptionComparator.classPK   }y�X7A�ϳ  !  F           ����  org/apache/maven/wrapper/cli/CommandLineParser$OptionParserState.classPK   }y�XO��4�    A           ����  org/apache/maven/wrapper/cli/CommandLineParser$OptionString.classPK   }y�XX����  �  K           ����  org/apache/maven/wrapper/cli/CommandLineParser$OptionStringComparator.classPK   }y�X����	  2  @           ���  org/apache/maven/wrapper/cli/CommandLineParser$ParserState.classPK   }y�X�Ć��  g  M           ��O�  org/apache/maven/wrapper/cli/CommandLineParser$UnknownOptionParserState.classPK   }y�X/���x  �)  4           ����  org/apache/maven/wrapper/cli/CommandLineParser.classPK   }y�X��sP  �  4           ��{�  org/apache/maven/wrapper/cli/ParsedCommandLine.classPK   }y�X�v�E�  a  :           ���  org/apache/maven/wrapper/cli/ParsedCommandLineOption.classPK   }y�Xx��͊  5  H           ��c�  org/apache/maven/wrapper/cli/ProjectPropertiesCommandLineConverter.classPK   }y�Xkn�4�  &  G           ��S�  org/apache/maven/wrapper/cli/SystemPropertiesCommandLineConverter.classPK   }y�X��c  H
  =           ��F�  META-INF/maven/org.apache.maven.wrapper/maven-wrapper/pom.xmlPK   }y�X�\�@   H   D           ����  META-INF/maven/org.apache.maven.wrapper/maven-wrapper/pom.propertiesPK    7 7 �  O�    
```

## client-service\.mvn\wrapper\maven-wrapper.properties

```bash
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip
wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar

```

## client-service\src\main\java\tg\ngstars\client\ClientServiceApplication.java

```java
package tg.ngstars.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClientServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientServiceApplication.class, args);
    }
}

```

## client-service\src\main\java\tg\ngstars\client\config\GlobalExceptionHandler.java

```java
package tg.ngstars.client.config;

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

import tg.ngstars.client.exception.ConflictException;
import tg.ngstars.client.exception.NotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                fe -> fe.getField(),
                fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalide",
                (a, b) -> a + "; " + b));
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

```

## client-service\src\main\java\tg\ngstars\client\config\SecurityConfig.java

```java
package tg.ngstars.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import tg.ngstars.common.security.RealmRoleConverter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
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
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new RealmRoleConverter());
        return converter;
    }
}

```

## client-service\src\main\java\tg\ngstars\client\controller\ClientController.java

```java
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

```

## client-service\src\main\java\tg\ngstars\client\dto\ClientResponse.java

```java
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

```

## client-service\src\main\java\tg\ngstars\client\dto\CreateClientRequest.java

```java
package tg.ngstars.client.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateClientRequest(
    @NotBlank @Size(max = 200) String companyName,
    @Size(max = 150) String contactName,
    @NotBlank @Email @Size(max = 150) String email,
    @Size(max = 30) String phone,
    String address,
    @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
    @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude
) {}

```

## client-service\src\main\java\tg\ngstars\client\dto\UpdateClientRequest.java

```java
package tg.ngstars.client.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateClientRequest(
    @NotBlank @Size(max = 200) String companyName,
    @Size(max = 150) String contactName,
    @NotBlank @Email @Size(max = 150) String email,
    @Size(max = 30) String phone,
    String address,
    @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
    @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude
) {}

```

## client-service\src\main\java\tg\ngstars\client\exception\ConflictException.java

```java
package tg.ngstars.client.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) { super(message); }
}

```

## client-service\src\main\java\tg\ngstars\client\exception\NotFoundException.java

```java
package tg.ngstars.client.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}

```

## client-service\src\main\java\tg\ngstars\client\model\Client.java

```java
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
    @EqualsAndHashCode.Include
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

```

## client-service\src\main\java\tg\ngstars\client\repository\ClientRepository.java

```java
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

    @Query(value = "SELECT LPAD(CAST(nextval('client_ref_seq') AS TEXT), 4, '0')", nativeQuery = true)
    String nextReference();
}

```

## client-service\src\main\java\tg\ngstars\client\service\ClientService.java

```java
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

    private String generateReference() {
        return "CLT-" + clientRepository.nextReference();
    }

    private ClientResponse toResponse(Client c) {
        return new ClientResponse(c.getId(), c.getReference(), c.getCompanyName(),
                c.getContactName(), c.getEmail(), c.getPhone(), c.getAddress(),
                c.getLatitude(), c.getLongitude(), c.getActive(), c.getCreatedAt());
    }
}

```

## client-service\src\main\resources\application-dev.yml

```yaml
spring:
  jpa:
    show-sql: true

logging:
  level:
    tg.ngstars: DEBUG

```

## client-service\src\main\resources\application-prod.yml

```yaml
springdoc:
  swagger-ui:
    enabled: false
  api-docs:
    enabled: false

logging:
  level:
    tg.ngstars: WARN

```

## client-service\src\main\resources\application.yml

```yaml
server:
  port: 8082
  shutdown: graceful
  forward-headers-strategy: native

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
    enabled: false
    path: /swagger-ui.html

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    env:
      keys-to-sanitize: password,secret,token,credential

```

## client-service\src\main\resources\db\migration\V1__init_schema.sql

```sql
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

```

## client-service\src\main\resources\db\migration\V2__add_client_ref_sequence.sql

```sql
CREATE SEQUENCE IF NOT EXISTS client_ref_seq START 1;

```

## client-service\src\main\resources\db\migration\V3__add_trgm_search_index.sql

```sql
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_clients_search_trgm ON clients
    USING gin (
        (lower(company_name) || ' ' || lower(coalesce(contact_name, '')) || ' ' || lower(email))
        gin_trgm_ops
    );

```

## client-service\src\test\java\tg\ngstars\client\service\ClientServiceTest.java

```java
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
        when(clientRepository.nextReference()).thenReturn("0001");
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

```

---

########### gateway-service ###########

## gateway-service\.env.template

```text
KEYCLOAK_ISSUER_URI=http://localhost:8088/realms/ng-fields
FRONTEND_URL=http://localhost:4200
REDIS_HOST=localhost
REDIS_PORT=6379
AUTH_SERVICE_URL=http://localhost:8081
CLIENT_SERVICE_URL=http://localhost:8082
INTERVENTION_SERVICE_URL=http://localhost:8083
MEDIA_SERVICE_URL=http://localhost:8084
NOTIFICATION_SERVICE_URL=http://localhost:8085
REPORT_SERVICE_URL=http://localhost:8086

```

## gateway-service\.gitignore

```text
target/
*.class
*.jar
*.war
*.log
*.iml
.idea/
*.swp
*.swo
*~
application-*.yml
!application.yml
!application-dev.yml
!application-prod.yml
.DS_Store

```

## gateway-service\hs_err_pid34412.log

```text
#
# There is insufficient memory for the Java Runtime Environment to continue.
# Native memory allocation (mmap) failed to map 134217728 bytes. Error detail: G1 virtual space
# Possible reasons:
#   The system is out of physical RAM or swap space
#   This process is running with CompressedOops enabled, and the Java Heap may be blocking the growth of the native heap
# Possible solutions:
#   Reduce memory load on the system
#   Increase physical memory or swap space
#   Check if swap backing store is full
#   Decrease Java heap size (-Xmx/-Xms)
#   Decrease number of Java threads
#   Decrease Java thread stack sizes (-Xss)
#   Set larger code cache with -XX:ReservedCodeCacheSize=
#   JVM is running with Unscaled Compressed Oops mode in which the Java heap is
#     placed in the first 4GB address space. The Java Heap base address is the
#     maximum limit for the native heap growth. Please use -XX:HeapBaseMinAddress
#     to set the Java Heap base and to place the Java Heap above 4GB virtual address.
# This output file may be truncated or incomplete.
#
#  Out of Memory Error (os_windows.cpp:3554), pid=34412, tid=16688
#
# JRE version:  (25.0.2+10) (build )
# Java VM: Java HotSpot(TM) 64-Bit Server VM (25.0.2+10-LTS-69, mixed mode, sharing, tiered, compressed oops, compressed class ptrs, g1 gc, windows-amd64)
# No core dump will be written. Minidumps are not enabled by default on client versions of Windows
#

---------------  S U M M A R Y ------------

Command Line: -Dclassworlds.conf=C:\Users\FOLLY\.m2\wrapper\dists\apache-maven-3.9.9\8e74001100ff70d6af083c5511fcc5ec49282d7017cde82c3698eee8fdf86698\bin\m2.conf -Dmaven.home=C:\Users\FOLLY\.m2\wrapper\dists\apache-maven-3.9.9\8e74001100ff70d6af083c5511fcc5ec49282d7017cde82c3698eee8fdf86698 -Dlibrary.jansi.path=C:\Users\FOLLY\.m2\wrapper\dists\apache-maven-3.9.9\8e74001100ff70d6af083c5511fcc5ec49282d7017cde82c3698eee8fdf86698\lib\jansi-native -Dmaven.multiModuleProjectDirectory=F:\03_Pro_IT\07_Clients\NG-STARs\06_PROJETS\ng-fields\Backend\gateway-service org.codehaus.plexus.classworlds.launcher.Launcher test -Dtest=KeycloakJwtAuthenticationConverterTest

Host: Intel(R) Core(TM) i5-6200U CPU @ 2.30GHz, 4 cores, 7G,  Windows 10 , 64 bit Build 19041 (10.0.19041.5915)
Time: Mon Jul  6 10:26:19 2026 Greenwich elapsed time: 0.080862 seconds (0d 0h 0m 0s)

---------------  T H R E A D  ---------------

Current thread (0x0000022efb933390):  JavaThread "Unknown thread" [_thread_in_vm, id=16688, stack(0x000000dccdb00000,0x000000dccdc00000) (1024K)]

Stack: [0x000000dccdb00000,0x000000dccdc00000]
Native frames: (J=compiled Java code, j=interpreted, Vv=VM code, C=native code)
V  [jvm.dll+0x7a70f9]  (no source info available)
V  [jvm.dll+0x772d6d]  (no source info available)
V  [jvm.dll+0x965d53]  (no source info available)
V  [jvm.dll+0x968351]  (no source info available)
V  [jvm.dll+0x968a63]  (no source info available)
V  [jvm.dll+0x2fc846]  (no source info available)
V  [jvm.dll+0x7a3325]  (no source info available)
V  [jvm.dll+0x796737]  (no source info available)
V  [jvm.dll+0x3f4988]  (no source info available)
V  [jvm.dll+0x3fc8d6]  (no source info available)
V  [jvm.dll+0x3e8f7e]  (no source info available)
V  [jvm.dll+0x3e9238]  (no source info available)
V  [jvm.dll+0x3c0145]  (no source info available)
V  [jvm.dll+0x3c0dce]  (no source info available)
V  [jvm.dll+0x92cdf7]  (no source info available)
V  [jvm.dll+0x458f02]  (no source info available)
V  [jvm.dll+0x9107a2]  (no source info available)
V  [jvm.dll+0x4f9331]  (no source info available)
V  [jvm.dll+0x4fabc1]  (no source info available)
C  [jli.dll+0x543e]  (no source info available)
C  [ucrtbase.dll+0x21bb2]  (no source info available)
C  [KERNEL32.DLL+0x17374]  (no source info available)
C  [ntdll.dll+0x4cc91]  (no source info available)

Lock stack of current Java thread (top to bottom):


---------------  P R O C E S S  ---------------

Threads class SMR info:
_java_thread_list=0x00007ff974725388, length=0, elements={
}

Java Threads: ( => current thread )
Total: 0

Other Threads:
  0x0000022efdbef990 WorkerThread "GC Thread#0"                     [id=23964, stack(0x000000dccdd00000,0x000000dccde00000) (1024K)]
  0x0000022efdc084c0 ConcurrentGCThread "G1 Main Marker"            [id=25360, stack(0x000000dccde00000,0x000000dccdf00000) (1024K)]
  0x0000022efdc09060 WorkerThread "G1 Conc#0"                       [id=34104, stack(0x000000dccdf00000,0x000000dcce000000) (1024K)]

[error occurred during error reporting (printing all threads), id 0xc0000005, EXCEPTION_ACCESS_VIOLATION (0xc0000005) at pc=0x00007ff973e04f1a]
VM state: not at safepoint (not fully initialized)

VM Mutex/Monitor currently owned by a thread:  ([mutex/lock_event])
[0x00007ff9747a9768] Heap_lock - owner thread: 0x0000022efb933390

Heap address: 0x0000000081e00000, size: 2018 MB, Compressed Oops mode: 32-bit

CDS archive(s) mapped at: [0x0000000000000000-0x0000000000000000-0x0000000000000000), size 0, SharedBaseAddress: 0x0000000800000000, ArchiveRelocationMode: 1.
UseCompressedClassPointers 1, UseCompactObjectHeaders 0
Narrow klass pointer bits 32, Max shift 3
Narrow klass base: 0xffffffffffffffff, Narrow klass shift: -1
Encoding Range: [0xffffffffffffffff - 0x000000007fffffff), (2147483648 bytes)
Klass Range:    [0x0000000000000000 - 0x0000000000000000), (0 bytes)
Klass ID Range:  [4294967295 - 0) (1)
No protection zone.

GC Precious Log:
 CardTable entry size: 512
 Card Set container configuration: InlinePtr #cards 5 size 8 Array Of Cards #cards 10 size 36 Howl #buckets 4 coarsen threshold 1843 Howl Bitmap #cards 512 size 80 coarsen threshold 460 Card regions per heap region 1 cards per card region 2048

Heap:
 garbage-first heap   total reserved 2066432K, committed 0K, used 0K [0x0000000081e00000, 0x0000000100000000)
  region size 1024K, 0 young (0K), 0 survivors (0K)

Heap Regions: E=young(eden), S=young(survivor), O=old, HS=humongous(starts), HC=humongous(continues), CS=collection set, F=free, TAMS=top-at-mark-start, PB=parsable bottom

Card table byte_map: [0x0000022efe060000,0x0000022efe460000] _byte_map_base: 0x0000022efdc51000

Marking Bits: (CMBitMap*) 0x0000022efdbf0120
 Bits: [0x0000022e977d0000, 0x0000022e99758000)

GC Heap Usage History (0 events):
No events

Metaspace Usage History (0 events):
No events

Dll operation events (1 events):
Event: 0.033 Loaded shared library C:\Program Files\Java\jdk-25.0.2\bin\java.dll

Deoptimization events (0 events):
No events

Classes loaded (0 events):
No events

Classes unloaded (0 events):
No events

Classes redefined (0 events):
No events

Internal exceptions (0 events):
No events

VM Operations (0 events):
No events

Memory protections (0 events):
No events

Nmethod flushes (0 events):
No events

Events (0 events):
No events


Dynamic libraries:
0x00007ff7d5e00000 - 0x00007ff7d5e10000 	C:\Program Files\Java\jdk-25.0.2\bin\java.exe
0x00007ff9d9f90000 - 0x00007ff9da188000 	C:\WINDOWS\SYSTEM32\ntdll.dll
0x00007ff9d87e0000 - 0x00007ff9d88a2000 	C:\WINDOWS\System32\KERNEL32.DLL
0x00007ff9d7c80000 - 0x00007ff9d7f76000 	C:\WINDOWS\System32\KERNELBASE.dll
0x00007ff9d7b80000 - 0x00007ff9d7c80000 	C:\WINDOWS\System32\ucrtbase.dll
0x00007ff9c39d0000 - 0x00007ff9c39e7000 	C:\Program Files\Java\jdk-25.0.2\bin\jli.dll
0x00007ff9d9da0000 - 0x00007ff9d9f41000 	C:\WINDOWS\System32\USER32.dll
0x00007ff9d7b50000 - 0x00007ff9d7b72000 	C:\WINDOWS\System32\win32u.dll
0x00007ff9d9620000 - 0x00007ff9d964b000 	C:\WINDOWS\System32\GDI32.dll
0x00007ff9d78e0000 - 0x00007ff9d79f9000 	C:\WINDOWS\System32\gdi32full.dll
0x00007ff9d7a00000 - 0x00007ff9d7a9d000 	C:\WINDOWS\System32\msvcp_win.dll
0x00007ff9c3510000 - 0x00007ff9c352e000 	C:\Program Files\Java\jdk-25.0.2\bin\VCRUNTIME140.dll
0x00007ff9bb1f0000 - 0x00007ff9bb48b000 	C:\WINDOWS\WinSxS\amd64_microsoft.windows.common-controls_6595b64144ccf1df_6.0.19041.6456_none_60b8a6cb71f64256\COMCTL32.dll
0x00007ff9d9650000 - 0x00007ff9d96ee000 	C:\WINDOWS\System32\msvcrt.dll
0x00007ff9d8db0000 - 0x00007ff9d8ddf000 	C:\WINDOWS\System32\IMM32.DLL
0x00007ff9cbea0000 - 0x00007ff9cbeac000 	C:\Program Files\Java\jdk-25.0.2\bin\vcruntime140_1.dll
0x00007ff9a1820000 - 0x00007ff9a18ad000 	C:\Program Files\Java\jdk-25.0.2\bin\msvcp140.dll
0x00007ff973a30000 - 0x00007ff97488f000 	C:\Program Files\Java\jdk-25.0.2\bin\server\jvm.dll
0x00007ff9d7fb0000 - 0x00007ff9d8061000 	C:\WINDOWS\System32\ADVAPI32.dll
0x00007ff9d9580000 - 0x00007ff9d961f000 	C:\WINDOWS\System32\sechost.dll
0x00007ff9d88b0000 - 0x00007ff9d89d0000 	C:\WINDOWS\System32\RPCRT4.dll
0x00007ff9d7f80000 - 0x00007ff9d7fa7000 	C:\WINDOWS\System32\bcrypt.dll
0x00007ff9d8df0000 - 0x00007ff9d8e5b000 	C:\WINDOWS\System32\WS2_32.dll
0x00007ff9d7480000 - 0x00007ff9d74cb000 	C:\WINDOWS\SYSTEM32\POWRPROF.dll
0x00007ff9bb550000 - 0x00007ff9bb577000 	C:\WINDOWS\SYSTEM32\WINMM.dll
0x00007ff9cd370000 - 0x00007ff9cd37a000 	C:\WINDOWS\SYSTEM32\VERSION.dll
0x00007ff9d7460000 - 0x00007ff9d7472000 	C:\WINDOWS\SYSTEM32\UMPDC.dll
0x00007ff9d5e30000 - 0x00007ff9d5e42000 	C:\WINDOWS\SYSTEM32\kernel.appcore.dll
0x00007ff9c9120000 - 0x00007ff9c912a000 	C:\Program Files\Java\jdk-25.0.2\bin\jimage.dll
0x00007ff9d5bf0000 - 0x00007ff9d5df1000 	C:\WINDOWS\SYSTEM32\DBGHELP.DLL
0x00007ff9b9e70000 - 0x00007ff9b9ea4000 	C:\WINDOWS\SYSTEM32\dbgcore.DLL
0x00007ff9d7850000 - 0x00007ff9d78d2000 	C:\WINDOWS\System32\bcryptPrimitives.dll
0x00007ff9bee40000 - 0x00007ff9bee5f000 	C:\Program Files\Java\jdk-25.0.2\bin\java.dll
0x00007ff9d9c70000 - 0x00007ff9d9d9b000 	C:\WINDOWS\System32\ole32.dll
0x00007ff9d9120000 - 0x00007ff9d9474000 	C:\WINDOWS\System32\combase.dll
0x00007ff9d8070000 - 0x00007ff9d87e0000 	C:\WINDOWS\System32\SHELL32.dll

JVMTI agents: none

dbghelp: loaded successfully - version: 4.0.5 - missing functions: none
symbol engine: initialized successfully - sym options: 0x614 - pdb path: .;C:\Program Files\Java\jdk-25.0.2\bin;C:\WINDOWS\SYSTEM32;C:\WINDOWS\WinSxS\amd64_microsoft.windows.common-controls_6595b64144ccf1df_6.0.19041.6456_none_60b8a6cb71f64256;C:\Program Files\Java\jdk-25.0.2\bin\server

VM Arguments:
jvm_args: -Dclassworlds.conf=C:\Users\FOLLY\.m2\wrapper\dists\apache-maven-3.9.9\8e74001100ff70d6af083c5511fcc5ec49282d7017cde82c3698eee8fdf86698\bin\m2.conf -Dmaven.home=C:\Users\FOLLY\.m2\wrapper\dists\apache-maven-3.9.9\8e74001100ff70d6af083c5511fcc5ec49282d7017cde82c3698eee8fdf86698 -Dlibrary.jansi.path=C:\Users\FOLLY\.m2\wrapper\dists\apache-maven-3.9.9\8e74001100ff70d6af083c5511fcc5ec49282d7017cde82c3698eee8fdf86698\lib\jansi-native -Dmaven.multiModuleProjectDirectory=F:\03_Pro_IT\07_Clients\NG-STARs\06_PROJETS\ng-fields\Backend\gateway-service 
java_command: org.codehaus.plexus.classworlds.launcher.Launcher test -Dtest=KeycloakJwtAuthenticationConverterTest
java_class_path (initial): C:\Users\FOLLY\.m2\wrapper\dists\apache-maven-3.9.9\8e74001100ff70d6af083c5511fcc5ec49282d7017cde82c3698eee8fdf86698\boot\plexus-classworlds-2.8.0.jar
Launcher Type: SUN_STANDARD

[Global flags]
     intx CICompilerCount                          = 3                                         {product} {ergonomic}
     uint ConcGCThreads                            = 1                                         {product} {ergonomic}
     uint G1ConcRefinementThreads                  = 4                                         {product} {ergonomic}
   size_t G1HeapRegionSize                         = 1048576                                   {product} {ergonomic}
   size_t InitialHeapSize                          = 134217728                                 {product} {ergonomic}
   size_t MarkStackSize                            = 4194304                                   {product} {ergonomic}
   size_t MarkStackSizeMax                         = 536870912                                 {product} {ergonomic}
   size_t MaxHeapSize                              = 2116026368                                {product} {ergonomic}
   size_t MinHeapDeltaBytes                        = 1048576                                   {product} {ergonomic}
   size_t MinHeapSize                              = 8388608                                   {product} {ergonomic}
    uintx NonNMethodCodeHeapSize                   = 5832704                                {pd product} {ergonomic}
    uintx NonProfiledCodeHeapSize                  = 122945536                              {pd product} {ergonomic}
    uintx ProfiledCodeHeapSize                     = 122880000                              {pd product} {ergonomic}
    uintx ReservedCodeCacheSize                    = 251658240                              {pd product} {ergonomic}
     bool SegmentedCodeCache                       = true                                      {product} {ergonomic}
   size_t SoftMaxHeapSize                          = 2116026368                             {manageable} {ergonomic}
     bool UseCompressedOops                        = true                           {product lp64_product} {ergonomic}
     bool UseG1GC                                  = true                                      {product} {ergonomic}
     bool UseLargePagesIndividualAllocation        = false                                  {pd product} {ergonomic}

Logging:
Log output configuration:
 #0: stdout all=warning uptime,level,tags foldmultilines=false
 #1: stderr all=off uptime,level,tags foldmultilines=false

Release file:
<release file has not been read>
Environment Variables:
JAVA_HOME=C:\Program Files\Java\jdk-25.0.2
PATH=C:\Python314\Scripts\;C:\Python314\;c:\Users\FOLLY\AppData\Local\Programs\cursor\resources\app\bin;C:\Program Files (x86)\Microsoft\Edge\Application;C:\Program Files\Common Files\Oracle\Java\javapath;C:\Python313\Scripts\;C:\Python313\;C:\Python312\Scripts\;C:\Python312\;C:\Program Files (x86)\VMware\VMware Workstation\bin\;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;C:\ProgramData\chocolatey\bin;C:\Users\FOLLY\.console-ninja\.bin;C:\Users\FOLLY\AppData\Local\Microsoft\WindowsApps;C:\Program Files (x86)\Nmap;C:\Users\FOLLY\AppData\Local\Programs\Ollama;C:\wamp64\bin\php\php8.3.14;C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2024.3.4\bin;C:\Users\FOLLY\.dotnet\tools;C:\Users\FOLLY\AppData\Roaming\npm;C:\Users\FOLLY\AppData\Local\JetBrains\IntelliJ IDEA 2025.1\bin;C:\Users\FOLLY\AppData\Local\GitHubDesktop\bin;c:\Users\FOLLY\AppData\Local\Programs\cursor\resources\app\bin;C:\Program Files\Microsoft SQL Server\170\Tools\Binn\;C:\Program Files\Microsoft SQL Server\Client SDK\ODBC\170\Tools\Binn\;C:\Program Files\Microsoft SQL Server\170\DTS\Binn\;C:\Program Files\Git\cmd;C:\Program Files\MongoDB\Server\8.2\bin;C:\Program Files\mongosh;C:\Program Files\MongoDB\Tools\100\bin;C:\Program Files\dotnet\;C:\Program Files\GitHub CLI\;C:\Program Files\nodejs\;C:\Program Files\Redis\;C:\Users\FOLLY\.local\bin;C:\Users\FOLLY\anaconda3;C:\Users\FOLLY\anaconda3\Library\mingw-w64\bin;C:\Users\FOLLY\anaconda3\Library\usr\bin;C:\Users\FOLLY\anaconda3\Library\bin;C:\Users\FOLLY\anaconda3\Scripts;C:\Users\FOLLY\AppData\Local\Microsoft\WindowsApps;C:\Users\FOLLY\AppData\Local\Programs\Microsoft VS Code\bin;C:\Users\FOLLY\AppData\Local\Programs\Ollama;C:\wamp64\bin\php\php8.3.14;C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2024.3.4\bin;C:\Users\FOLLY\.dotnet\tools;C:\Users\FOLLY\AppData\Local\JetBrains\IntelliJ IDEA 2025.1\bin;C:\Users\FOLLY\AppData\Local\GitHubDesktop\bin;C:\Users\FOLLY\Downloads\flutter\bin;C:\Program Files\JetBrains\PyCharm 2025.1.3.1\bin;C:\Users\FOLLY\.dotnet\tools;C:\Users\FOLLY\.bun\bin;C:\Users\FOLLY\AppData\Local\Programs\mongosh\;C:\Program Files\mongosh\;C:\Users\FOLLY\AppData\Roaming\npm;C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.3\bin;C:\Users\FOLLY\AppData\Local\Microsoft\WinGet\Links;
USERNAME=FOLLY
OS=Windows_NT
PROCESSOR_IDENTIFIER=Intel64 Family 6 Model 78 Stepping 3, GenuineIntel
TMP=C:\Users\FOLLY\AppData\Local\Temp
TEMP=C:\Users\FOLLY\AppData\Local\Temp




Compilation memory statistics disabled.

Periodic native trim disabled

---------------  S Y S T E M  ---------------

OS:
 Windows 10 , 64 bit Build 19041 (10.0.19041.5915)
OS uptime: 17 days 23:57 hours

CPU: total 4 (initial active 4) (2 cores per cpu, 2 threads per core) family 6 model 78 stepping 3 microcode 0xcc, cx8, cmov, fxsr, ht, mmx, 3dnowpref, sse, sse2, sse3, ssse3, sse4.1, sse4.2, popcnt, lzcnt, tsc, tscinvbit, avx, avx2, aes, erms, clmul, bmi1, bmi2, adx, fma, vzeroupper, clflush, clflushopt, rdtscp, f16c
Processor Information for the first 4 processors :
  Max Mhz: 2400, Current Mhz: 2300, Mhz Limit: 2280

Memory: 4k page, system-wide physical 8071M (234M free)
TotalPageFile size 32647M (AvailPageFile size 26M)
current process WorkingSet (physical memory assigned to process): 11M, peak: 11M
current process commit charge ("private bytes"): 52M, peak: 180M

vm_info: Java HotSpot(TM) 64-Bit Server VM (25.0.2+10-LTS-69) for windows-amd64 JRE (25.0.2+10-LTS-69), built on 2025-12-18T11:36:35Z with MS VC++ 17.13 (VS2022)

END.

```

## gateway-service\mvnw.cmd

```text
<# : batch portion
@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.4
@REM
@REM Optional ENV vars
@REM   MVNW_REPOURL - repo url base for downloading maven distribution
@REM   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
@REM   MVNW_VERBOSE - true: enable verbose log; others: silence the output
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
@SET PSModulePath=
@FOR /F "usebackq tokens=1* delims==" %%A IN (`powershell -noprofile "& {$scriptDir='%~dp0'; $script='%__MVNW_ARG0_NAME__%'; icm -ScriptBlock ([Scriptblock]::Create((Get-Content -Raw '%~f0'))) -NoNewScope}"`) DO @(
  IF "%%A"=="MVN_CMD" (set __MVNW_CMD__=%%B) ELSE IF "%%B"=="" (echo %%A) ELSE (echo %%A=%%B)
)
@SET PSModulePath=%__MVNW_PSMODULEP_SAVE%
@SET __MVNW_PSMODULEP_SAVE=
@SET __MVNW_ARG0_NAME__=
@SET MVNW_USERNAME=
@SET MVNW_PASSWORD=
@IF NOT "%__MVNW_CMD__%"=="" ("%__MVNW_CMD__%" %*)
@echo Cannot start maven from wrapper >&2 && exit /b 1
@GOTO :EOF
: end batch / begin powershell #>

$ErrorActionPreference = "Stop"
if ($env:MVNW_VERBOSE -eq "true") {
  $VerbosePreference = "Continue"
}

# calculate distributionUrl, requires .mvn/wrapper/maven-wrapper.properties
$distributionUrl = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionUrl
if (!$distributionUrl) {
  Write-Error "cannot read distributionUrl property in $scriptDir/.mvn/wrapper/maven-wrapper.properties"
}

switch -wildcard -casesensitive ( $($distributionUrl -replace '^.*/','') ) {
  "maven-mvnd-*" {
    $USE_MVND = $true
    $distributionUrl = $distributionUrl -replace '-bin\.[^.]*$',"-windows-amd64.zip"
    $MVN_CMD = "mvnd.cmd"
    break
  }
  default {
    $USE_MVND = $false
    $MVN_CMD = $script -replace '^mvnw','mvn'
    break
  }
}

# apply MVNW_REPOURL and calculate MAVEN_HOME
# maven home pattern: ~/.m2/wrapper/dists/{apache-maven-<version>,maven-mvnd-<version>-<platform>}/<hash>
if ($env:MVNW_REPOURL) {
  $MVNW_REPO_PATTERN = if ($USE_MVND -eq $False) { "/org/apache/maven/" } else { "/maven/mvnd/" }
  $distributionUrl = "$env:MVNW_REPOURL$MVNW_REPO_PATTERN$($distributionUrl -replace "^.*$MVNW_REPO_PATTERN",'')"
}
$distributionUrlName = $distributionUrl -replace '^.*/',''
$distributionUrlNameMain = $distributionUrlName -replace '\.[^.]*$','' -replace '-bin$',''

$MAVEN_M2_PATH = "$HOME/.m2"
if ($env:MAVEN_USER_HOME) {
  $MAVEN_M2_PATH = "$env:MAVEN_USER_HOME"
}

if (-not (Test-Path -Path $MAVEN_M2_PATH)) {
    New-Item -Path $MAVEN_M2_PATH -ItemType Directory | Out-Null
}

$MAVEN_WRAPPER_DISTS = $null
if ((Get-Item $MAVEN_M2_PATH).Target[0] -eq $null) {
  $MAVEN_WRAPPER_DISTS = "$MAVEN_M2_PATH/wrapper/dists"
} else {
  $MAVEN_WRAPPER_DISTS = (Get-Item $MAVEN_M2_PATH).Target[0] + "/wrapper/dists"
}

$MAVEN_HOME_PARENT = "$MAVEN_WRAPPER_DISTS/$distributionUrlNameMain"
$MAVEN_HOME_NAME = ([System.Security.Cryptography.SHA256]::Create().ComputeHash([byte[]][char[]]$distributionUrl) | ForEach-Object {$_.ToString("x2")}) -join ''
$MAVEN_HOME = "$MAVEN_HOME_PARENT/$MAVEN_HOME_NAME"

if (Test-Path -Path "$MAVEN_HOME" -PathType Container) {
  Write-Verbose "found existing MAVEN_HOME at $MAVEN_HOME"
  Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"
  exit $?
}

if (! $distributionUrlNameMain -or ($distributionUrlName -eq $distributionUrlNameMain)) {
  Write-Error "distributionUrl is not valid, must end with *-bin.zip, but found $distributionUrl"
}

# prepare tmp dir
$TMP_DOWNLOAD_DIR_HOLDER = New-TemporaryFile
$TMP_DOWNLOAD_DIR = New-Item -Itemtype Directory -Path "$TMP_DOWNLOAD_DIR_HOLDER.dir"
$TMP_DOWNLOAD_DIR_HOLDER.Delete() | Out-Null
trap {
  if ($TMP_DOWNLOAD_DIR.Exists) {
    try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
    catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
  }
}

New-Item -Itemtype Directory -Path "$MAVEN_HOME_PARENT" -Force | Out-Null

# Download and Install Apache Maven
Write-Verbose "Couldn't find MAVEN_HOME, downloading and installing it ..."
Write-Verbose "Downloading from: $distributionUrl"
Write-Verbose "Downloading to: $TMP_DOWNLOAD_DIR/$distributionUrlName"

$webclient = New-Object System.Net.WebClient
if ($env:MVNW_USERNAME -and $env:MVNW_PASSWORD) {
  $webclient.Credentials = New-Object System.Net.NetworkCredential($env:MVNW_USERNAME, $env:MVNW_PASSWORD)
}
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
$webclient.DownloadFile($distributionUrl, "$TMP_DOWNLOAD_DIR/$distributionUrlName") | Out-Null

# If specified, validate the SHA-256 sum of the Maven distribution zip file
$distributionSha256Sum = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionSha256Sum
if ($distributionSha256Sum) {
  if ($USE_MVND) {
    Write-Error "Checksum validation is not supported for maven-mvnd. `nPlease disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties."
  }
  Import-Module $PSHOME\Modules\Microsoft.PowerShell.Utility -Function Get-FileHash
  if ((Get-FileHash "$TMP_DOWNLOAD_DIR/$distributionUrlName" -Algorithm SHA256).Hash.ToLower() -ne $distributionSha256Sum) {
    Write-Error "Error: Failed to validate Maven distribution SHA-256, your Maven distribution might be compromised. If you updated your Maven version, you need to update the specified distributionSha256Sum property."
  }
}

# unzip and move
Expand-Archive "$TMP_DOWNLOAD_DIR/$distributionUrlName" -DestinationPath "$TMP_DOWNLOAD_DIR" | Out-Null

# Find the actual extracted directory name (handles snapshots where filename != directory name)
$actualDistributionDir = ""

# First try the expected directory name (for regular distributions)
$expectedPath = Join-Path "$TMP_DOWNLOAD_DIR" "$distributionUrlNameMain"
$expectedMvnPath = Join-Path "$expectedPath" "bin/$MVN_CMD"
if ((Test-Path -Path $expectedPath -PathType Container) -and (Test-Path -Path $expectedMvnPath -PathType Leaf)) {
  $actualDistributionDir = $distributionUrlNameMain
}

# If not found, search for any directory with the Maven executable (for snapshots)
if (!$actualDistributionDir) {
  Get-ChildItem -Path "$TMP_DOWNLOAD_DIR" -Directory | ForEach-Object {
    $testPath = Join-Path $_.FullName "bin/$MVN_CMD"
    if (Test-Path -Path $testPath -PathType Leaf) {
      $actualDistributionDir = $_.Name
    }
  }
}

if (!$actualDistributionDir) {
  Write-Error "Could not find Maven distribution directory in extracted archive"
}

Write-Verbose "Found extracted Maven distribution directory: $actualDistributionDir"
Rename-Item -Path "$TMP_DOWNLOAD_DIR/$actualDistributionDir" -NewName $MAVEN_HOME_NAME | Out-Null
try {
  Move-Item -Path "$TMP_DOWNLOAD_DIR/$MAVEN_HOME_NAME" -Destination $MAVEN_HOME_PARENT | Out-Null
} catch {
  if (! (Test-Path -Path "$MAVEN_HOME" -PathType Container)) {
    Write-Error "fail to move MAVEN_HOME"
  }
} finally {
  try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
  catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
}

Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"

```

## gateway-service\pom.xml

```xml
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

```

## gateway-service\.mvn\wrapper\maven-wrapper.jar

```text
PK
    }y�X            	   META-INF/PK   }y�X�1Oe�   J     META-INF/MANIFEST.MF���
�0����]�N� A����Fss�}aA]���|�0��>�=�^Y�!f�<����7����"��VGe eˉ��%-�VIL���V5"�_�VA����s�~ν�)K?7#��P�2'�1*ۆ��k;��H���M�����n�|��ӓ�PK
    }y�X               org/PK
    }y�X               org/apache/PK
    }y�X               org/apache/maven/PK
    }y�X               org/apache/maven/wrapper/PK
    }y�X               org/apache/maven/wrapper/cli/PK
    }y�X               META-INF/maven/PK
    }y�X            (   META-INF/maven/org.apache.maven.wrapper/PK
    }y�X            6   META-INF/maven/org.apache.maven.wrapper/maven-wrapper/PK   }y�X�G�q        META-INF/DEPENDENCIES��A
1E�=E.����N�u���4	i�����v��!?g:�Lʙn�ڤ�*ph�΂F�PJ#[1�!;����V��~^y����ŃU���_߁�����ʦ��k
vGЅ#| PK   }y�X���m  ^,     META-INF/LICENSE�Z[s��~���r�Si��4i牱䆭CiD�n&��%� ���. ���=����dw�VM�5I�ٳ�����E/˝�u�:�^<��?�u�t����B�Mv�������<�h7��W����\�6sc���ʽz���~Z���J��Y]-�7��xws'>��qw}{ws��-~]�SW������	�j.�T�;=�rn��k3�'�	��M#Z%;1�Ie['dW��t���bt�V��Tc�_^>[i7X��{!��pKU��A�U�B��֌۝�N�>hxΔc���X/cO+M�z���w�
P	�� �8�������s+��l��v[z��!S@me#�I�c�$핐%I	Z��Y/��^A�o�i
!�
R����cW��Ҵ��$����a�rxùxg,�я�71ɪ���G3/eFGq�B_�R�W� �Y�*�;�w!#J	N����,`E+;�U�<�׍��+V��N������$ٹe��	�\hЄ��v�GI������%������/i;�a�A���:� �d�A�Fu`�R�+'�3=��6�L\�Z���]�^���&�Q�y|x����[�<�'��$�ְ[	)��GZoU�����kM��[���p4IY�������3�ht�qw�3����r�!8���#A^?P����v��;��Q|�l��p�����cl(?jkZ�����	Q�9|R���o��R�yH\1=��qtLH�^cBR�s� g��'��N����P�n�*-�p��c4��	(��KҘp#-����1b����ZY�<H��M�?å���>�dą�n`x8�[
�dV9X[�BA[/��e��ΰ�Ⓥ�W��#$Sc���
W����h7;� ����$�AP|#:��T�p�~��*܊܅����r��8k� �iՃ&Wb�i|�66|��y6yaX唃H!�K��4��Lou�����NՓ�/ı���0���H��V�R��T��)h:F��j��'2��㤓��N� D��%�"��Ѩ'J�u�����"�����9S6�/�'\��Q6�	�p�H�d�6�
~J�"K�Q���M�m7n ;<x�A�E��z>h#��Z�L���j�De��}���5��i��e�^��f^��˰H5��� 腍l(���uD>��[_`�FW�Ph���d!����R�+��K:"�7@)AZV�"r7���5wTXBJ���	v?V>f+�k�F/2�DAfm�p�rtT�iǖ���ȏ�x�4��`��YC<�Q\��ь����B�M�(P.��#�PD�a�F"��l��"����4���u<v���R�܀���Ѧb�l�PFEHJ���$t���mK��r��7K?����H�p۷���Y��������f&K��TI�H ����� 9�S��� �	���T{�\�3�K��Ǘ�z�'s��pxY[�4�S"��Ts��ᆡۂ�c=��	�%8���+B����@�߀�\j}�E޷�4?b1��ϔs�v�3�J���\�2��`�r�"����K��Y3�]a;�������6u�<��j ~�Q��1<Q���`&�M�>
�ʾo��48�����U+����lv8�"	ɭq���uNZM�Y[@���(j_����`�)_���DVOˎ�q��-��$o���b���n.�5�?�B�
c::e�[VAn%�L ���T�"��ƹ�d0<FiF�O�</E#�n��Q[.`��|�G���QM`ŝo���29����S1LŦ�(ShF}��F#�/y�Uqu�E�X�.�
���ҰO�
���;�O��u+	َQpPn3��gX�i#l6�Q!���7�"O�f.�O Y�Z!2H
�V)�rm艸��z�셼䓎i[���~ܪ�Z9���!��TR}8�$��2��d{��&Qi죰硎���Aw'�=�l{���([�-C����e��U$Xxs��Sw .�8n���Kձ��] ,V
yS��	
�!��?� ��s�����g�A�U�-T<&��3��p�INK��h�%�V�o��ճ��������q {c��=�rg��ٕA��L9�,�+ZO	>���)��Y�"(I��fb<�2�A�ŗ�5s��g�J�2%�S���/I�
�6}ԔA�d�d�IT�gu�>�I��y=@	]'����M�T��ũ�e�zٔ��g�Te
� �Y �V/���s�0#�P���wa�_�f��M�[�8�"5��P����"�:Lf�lȪ�[�w�̤ս��$
��G�g�~
�U��jlm�DL ���;�1��`���D�*虘��8��0O�[�5Q�*��Ұ�	���+s
���UƑ�F�:a�g|흹2b1�]���hS����Y<<ъ�ӹ�J$�ΦyI��۪I��g�D�1�&c�ةu�|K͎�	�^5�@7:�����a�Rc�K��8�8��l�����]%��;r��m���Ӛy�Ejf�"��V���ׯ̀���՗���vK��R͍P��_ad.�1��)X1�D[��(�>C�#S��� ��7Ī��|�t�{���?�3]B΁)wv#���jL_�5�lqnN��}����:��C��C��6ժ�F�o���;�	�tr)~���4jV�Q��+bӁ�ړ�lȦ�7_Δ �ԟ��J;j��Ҷ��]1	���7��yc��`��H�K���a>�]R�uš�q��?��ˉs/q��?[��r=?,��u0�����7������bu��^����Z��X�~_����h�~��K'ф+U6&MDsRp� M.��"{
�`��������z�\��[��z����?]߽��\��|����B���~u���^�������N�~���Y_s����o@�6�t�@73�N�<gMo5�s:pх�P�%���<mt87��v��Δ:���������E�i3˱��9|&�E���.ϗXyПn =X|�аt�N;���,�!tj�h`_��,�mw1����g�������!BG�mq�-����v�|~0zN�e��M�� �V�r;�����J@z9��
�ֳ�gH( �|���g�x!��ƙ��j�w�X�c��[��F��9F���ygf��O.��Z���5���&�~��l�^�9����R7��j$�z���"x�M������+��q��x�e�a��4]����� o��r����\,J�	h�����"�,)>O��������B˝1<�I�䲝f���jExPGʮT|��Ǡ�w���Ւ4c�6Awa6��Boy���̗�Z�<�/���Ac���c'ĭd4�3��Go�tMv9���!���4�(�KL'ݢ$DO��,�L{&]3>c�s��m�h�J�Ю�
`�ՙѹ�-!Q �ъ)�Gk�m��&CW��*Q�ӹ����F:�-�l��>�ƌ6F]8��WWXWϽG�/no��?ߠiZ �z�/���o��>�%���.(�k�iB����B>��F�:�Z��r
$;��o)D��_g�h2��!�����Nz..�L����@��A��.u�Ԧ:�	@��;��vv7������"��zV p6/��i?'(N�r�@�!c嶋hf�q�Zݨ��
ݐM.��r4�F�a���|��_PM<���½kϤ!���o�9�e�/��U�Bz��G�����>H��g��O��*.������("�#\���<�x��6��1FT�8"u�fC�29م@�C�Ͻr���j}�T�%_�П���3���N�p�K������H��&�������h��ێp@	�,t�o��iI����\��PK   }y�X��w��   �      META-INF/NOTICE}̱
�0��=Oq���:���Ap��|4g(��rm�����_>����(B
WTwayi��A�����0D����ɝ�VQ	z^r@K��sCLD9,�A��*P~6�J3@s�g��frj�Z��/�S���PK   }y�X�۱A�  U  3   org/apache/maven/wrapper/BootstrapMainStarter.class�Wk{�~ǖ���	�pJ��M�eǖJ 4�!B��vj�R��t%��M�]e����Ҧ)�׶h�Wz/��/��~���~)}g�U��:<�#��9��9������G �G1t�SAHE�m�-S�̹�d�^t"#�i8�t��O�E����n�C�=�ѪZ��g���,�Z��ۙ�,˩9��k��w4��m��f����&�c)����iX�Y��gNhNy�?��`����ɜ�ʍV�Z-gi%�V�M@m_�F�+�Q�;�5
��m�픷�+;6ÝH(إb7��+k�M�6��(v�U�T�|V`���l3U�gNw�j�>�#'<u�%\��S�+�vQ��f�������S3~S6�$C8��
U!��ɲ�k%�-ź-�{��]�T�y�c���Ψe:���shWjS+�xP�~�O�h��V���}�CQ�-Zv�TKW����rF����/� ��P��U��}��c���0���
��DM��y]�Ȉ������X�5�Rw�Fn�Lf��yT�a<&u��^�1jZ��xB�O��F����	�b�8�xqk���(�Q};rE˜��9�]0���>� m�P1�����] �9v~ף�ͳ��d1��W�i��g�`��K*�q����S��W�l<�!h����l�M)��@�O�i_Vq_�s3F61�\�.�ك�{6Z�o�&�#)��/��)(y�� ��ɪcXf��SPVa�<�����m�7l��l�}_`��m��p��(*��Q2Cm�H��li���B����[{e]c�=����:E�������o�͂1xh���W�1*��&�
�pI�WU|_������[�j�5�tVJ|Cųx��1]����~Sŷ�1.V�����^|��f-j���|G��x��P+���j��R�e�S� 	v4�� ��2��sĪ��'����.|?P𒊗��\�rVQ�0=CS��,�xn���5��Q�^)%M�IVHv�S֓㲡%�^�<vx*i�Mj��)l�r�d_o�/��#?V����R��Ef-{^#��S�u��O�	����a�6ϋ�����Y���L�'���>)��x��Ok�!�Mb�),%��\B���|�w��fx��i�w�rw8�Ãi��$wY��M�p*&|u���7�晶}C+t/��ڮ�;#��٥�[�z�#�Hͭ,e=ો�JK�_�%�{��
<��i$Pi��[��Tn�^�0�U]����"��d�_��U����Z((o�R�׺(/��B^���	g��cx`�:_:�S>#.���6�.~�Q����(,�ɧ�vy`p��l���mӝCk�.��;�{{Vp_6���Dh�D(��2�g��k��k8$���3����5�w�OO��_���tg�T~g��DD��1ݹ��3�]�����Ѱ���x���^^���}&�/3��\��s��:z��L�Q��K�v���_��t:����:��)��E�Z��w�=��m��H�?5ak���k�"�:���	�EF%�{����`�MW�_�Ӄ���� ���Dh��p�|{F��g�C�D9.3���9�kL��=��;���'B��2�&B�����{�W=W��
���V��A^z���s�!�4m���=Ȓk{0BT&�F9���+�;�7Z�<{x���Zvs�����-��������E",��ɱ!�Ғ\���!�	��(�*��?~B-��7:r
�|���ׯ@�%C+���n���/PK   }y�X܇�H  C  2   org/apache/maven/wrapper/DefaultDownloader$1.class�TmOA~
�ak�"��R����O5��Zb[�m��.��.�^!|�'��h��Gg�HP �Knvfn�yfgg����?<�3CY��.��͎p�|OHw_��]�ݲ���}9�F,�:�0ư|.� vx?j_���N�1����^%\����������+E���aG��o�Pi��ܗ~��0���-$f�C����W�u�+9�$�d�^i�[+nV�墍똲�r0��(C�eۭ�B�%����cXȔN|�/۹�i�n�%��k���͂�۸cᮃY��Ka&B�u��k�b��g#�����gPoy�:���Lk"橖`��|)*���o�v@�dI5yP��7��3f��a�,��,jU��]꟦3�ȳυa>s������5��M��7��N�����l�G=�`�Fq���;xb�n�����k��Ά�B{�)z��+b��b���$��;�4�H[��x�ť/^<���(f��(�xH25�BY �L6���
�� �%,�ˤ}E�O�@�]��7\Kΐ��[����!Ia�GLL�H��El� |�6�Gb.�1��1�,L,���PK   }y�X�4'0  �  S   org/apache/maven/wrapper/DefaultDownloader$SystemPropertiesProxyAuthenticator.class�SkkA=7M�q�>\5��Z�Z�X��VQ"BI�TՂ��4��d7�N��W���?J��Y��pv��9���wf~����2��H`�B2�a��UwE[��m�]�{J��R����4�j��7�#���A�ek]��=r�����k�&t�#/<��/	C�����ѺD�|����v��-�����E�j�������ŶQ|Jec�.���
a2�h
���LF�R�V��)�V�_/�ϧR�FpN��J!K��2�7��^H��-ܶp'�i̜uع*�V��<��oUl֞5;6G��Qު�7Z	v$a����M��-�;���Sj��)�g�q2�^Hx[��'�L�2�ﻓ��\ܒ�o73�r��*���'������F�Q5��3ɜ��WB���KUi�_ZxD(\\�0��YxBX�<f�"�`�!��2�0�3�G>�.���A��;%�b��� ���1�<
q�g�Z�;�N��q窓���!n���c:�ˀC�=���p|B��QO�1�s�/��+�ľL���QY1»X�����/�r��oPK   }y�X�3�    0   org/apache/maven/wrapper/DefaultDownloader.class�Y	`י��-k�bB@lAJ����C.;68��W-cc �A�yF� w��&i�6=��^��3i�*��p�݋�v�{�g��}d�nw�f�73�%[N	�y�~���+ v�c
�Z-{&���Ԭ���Ofⴭ�r����Io0�g�hPԠ@XsB?�'��9�9~�H9�{3fƹ_���u�!�R�i���V�ט�Yg�u��Zzڰn�J6�ҝ�e�s�@xp�]ұ3�L�d�F�ZI=\q`°�|��ѰN�zS�2�33���3�g�0�ܸAC���T�X�GET`�����9I�@ްC؄��E�f�\e��|�1�V�RIg^`G�rZ�O)�E`��[k����1�KU�(i[�۶	�l��;{Z'B؁[�hhE��Wb�pU<By��)l�]��Ut�041<y�@�l�w�_�2�!!��󔊝�]�������^�t\��!t������ќb*�[���"c%�3Y#1�1O��<�t���W�} �#���O��g2y�/]%s�6���l�������Z���UЧa����4J��6�K������A����C�/8�����Ti���X���1�flF�eg�6coJ���ԔNŠ@���|�Q0�����906�i���d.X�R#lB�&q�:����,�X_�^Ux�@�{c�˘y#�<cF0��N�9V,�#$FH��K�Ů����f=��g��X�%�o||4�QpT�1�M���8h���<8����K�cB��RHSݒ\�cئ���+"�&�e~Id�f@458ց1�cMI��A��N*�j��Y�n_��c�)[I`��k=R朆GA�6��t��Ϥ�S-KRmɗ�L�h(H��Sz6U��29�ΐ���LKr��C���|����;*3Y]�b&#Z�|E����j�E�;YG�.�2ٴ���AF���a-K��Y���S�ƻ	V	����e|}��
����>I��K�XvJB� >���~gY��}�v6AD3d�I��29�-Z|L7�K�⯱З����\f?��#���jg�AK*���!|�PpNÏ�G��Ey�=k�UF،i�K�e����S���3��F����H����K_|N����`y+{��ee�U�D�s��/QF�r��0tڤ�d	�X`J��e�e����R#~
?�����y�X���C��vzq?m1�?:ػ��X�������C[��X���_��U�@��8/�PKMy�L֯ ,�׍��"I�YҢS5k��R��K��5\�\��Lm���7|����,i�֏gyW�s~����i)Y����� l��B���|�qM���%�>mdٵL��������d����_���[���i��[��j"��)�L���o����d�p�iLr�m�#[)�8kѝ��͔�6d��{~@��{�z��F���;I���?R����v|ɺ�u�u��أ˩xK���r�!���7�
�R�_��5ϲ�˚������]��2�S<Oi[������5C�F��A��2j��mc:���(��?�Q\��6�����w0���.Pw�/���*�/��o�2Ati�<e�d��S�
~���O5���_���;���-eT�fu�un15xc��������n\����v[�i,5��R߼#;��-+i=ܧ�����?�2��VE�������e��3f:�Wwtg>g�2�b	�M� �є�g쾌���h���T�
�%�
���Z{ٍ-����g�m�b^U7%�L>fZN,��`Q�t�s�V������*n�-C�{��	�	�1���b-o�-{^�(��&���%�R�P�N��Bi�j��3N��`Ul��7~jnq�&V;+7�u�b3���b�[���l��"�*b�&�����-�n'2c##���y�R�*ZxܞOl��Z��{��[+^	"Ѧ�v��`pڲ�t��,�����bSԣ���֕/}_{���`�pa�a�K/IŤ���c2��f���P2g3�R'��OE-5�7��,8�����M�%нZ��VY���h'	5c������-V��.�5���&�4��q�H��\O�قk
c.�����w�,%KU4�j�\��,��R�۸r�%��\ZګxC��ː�_�c����lF�Z?X#�6�U�1XBD�
��5^���t?N�9.��l����PH
nd�qZ^Z��T�Zy�cW�����e �X��&e���.���i�Z�v�	y��-�����CX~��\�>AS�\Mp��z>��.����4�u��9
����T'n��F�� ��Hb'Iu���؀]b'Wb�%n�����d\���A�َp�\[n^��kX}	�)q[�]�wy�vi��Sw#�n������]r/D�ě��g|����輆�������˒Y���U�l�{-w?{ ͼFl�@�m�L���H�>��YNIi�:�/���	��x\�ѿ��&��1TD������w\��(�f:�*��5���c؁$�1A�OVع�l�.q����K�-�4R� �͢�}B�>U�T�JO���Jcw����Ga�(��h |*�P�O�Kxl�"~����xW�=E<���C��ß�O?�">[��`4Pĳ�JT����S���\8vTN'��5�*E��6w�r��jݍQ5�X��Fի��~q�9��y������Q<���U�6��|N�K�@�.�
t�l�1�8D���G��G�ܣ��atb�8�}F������@Ӥ=C�iҜŗ(�|���8�zb��;��E����ە�O��}r��	y�(���w����M���%��uF�q_�������� �`X�n'�+w�/A(��6�����&�ۨ��g���=��Q�~�|��|���_�/w�,!�5���ߠ[�:/�<�K�e�P21�z-�]ôyD��h-��U�S�:��*���*̸���w�q���	'�iT�rζ�E��PG�ϊ�F�oN��w����!�^���[��݁N�u"�U��hS���?E|�;^���wD�|#"�Շ�Q�@w�P�""�(�Uʩ��4��O�I>=�t1l�wr�q��	l��]�OѭOs�{���$����8�~�bG�R�����83� �<��ô�I4�sO�1(���dt�M&��ek�u-�
�gĈ�B;�^��.d�o �S�!P��;K�x��1r�#�Νn2rS�p�5�̄����5�Eqc�����E>�+�7?J�|���qt�\Vv���)��b%�v1��E���U�E�+��K�:yr~�GT>O�}�-_D�g���&>�#"�Gw "6�gCD�E>�q�|*�-q��u=�7&��ɩ�hCr*&��̒SjGrjM��?J4�\����q���S��Ӑ��"�,}�9֏���_`�|��2(��m�Ÿ�+��W��	��m¨��[�T���n�h�n1ŷ )�-�M���aW�#���ױD��Q�'q��?PK   }y�X�y]�   �   )   org/apache/maven/wrapper/Downloader.classE��j�P����

>���lܹm���~LF�����u��C�%��a�p������Z��u'�B�D�,5\9)
u�l+�[���F�\�s1'~?d��~\#��l��-y�y[�*�|Ls嵔�r�'�/_�ej�G�¸�zw<���.�WJ6A�nt�@h�V�su��:��g�w���PK   }y�XK>8ڤ  {
  4   org/apache/maven/wrapper/HashAlgorithmVerifier.class�V]wE~���4]�J�V`)��Z%A��@J��B���d�lIv�f��
~r��x���xQzD�o��x<G~�zA}gӔ����vfv�y����y���x��h��ï � C�>�Ǌ����LN���<j��s�����B#GXA�^�6��ذ�T�8e�E�$���I�G73��`$�Lyڱ3���^� ���a��0+�3�"6R�H�1,3�fl�hQ�[����Pa�h���Yv2(��В���SHL�ֳRs�NV�I��m��h�_�a�ӔeD&�I��K
v`'Qꖳ�C�p:J&%�۠)�-S�����SS�6Z��}�(��D��}�ϰA�f�n�lm�,m_I�D������&�٥��#щA�^���3�Ŭ���ALVF�D��!�UW��a*�W8��׈�gg�Ȧ��+FrDd0�Z}L��A�=��u[UuU��eβK:yy�N�L��/�Dt-Jz���$uz�RJ��_�k���<��4���e:�tN_w�"�V�JM'���]A���^i�$9�*8�T->�1����ыď����(CK���D��czѐ5�պ*ZA�4"D�K���Nh��JT�xA��ù�Zu���4F9�V0�K�,;��z� b%}Z��[rj��|^��a�,��z�q�3�?;�蚎Q�od�w�B�`�nH�˗�!N/��K[Z֨��IWB��m�r�zg%#_p$A�T���A��OK椀V�uٕ��®���P3E�x�9�U�"c�jH!����q��{q��=��u�9C�;Q�[�Jc�l�a��251z��R�lJ�8�&�=*ﳬ��1�6��Ҧ�)T�����M�.Ǵ\�2�}�����D�H���H&�,
3�X�z���l����[�W_F�~���z.2�j�Y�S+�ah�k��mq��J��v�k�{ٕ�O*�8t�<u������g��e/-S�Ok�f/����U���	��T�1�E�^7�}R���Uj��Sg�35t'�+F3�9p��]�8Cc��܉,�J ��yE�q�
�=�] ��}�a����~
ŹT�<Z�B_�Y����G����j�1���y��N�|��K�J<�r�p{U�H�#�Gϸ|(�C�D$�p��7n�6~��j�5|x�I�-��0�kmZ���������a6�������?�͖᠇�M������O�\�î��<.!Wːyz�<�K�7��S��7�<�>��A�}��V{�F���	%�\�&��5�;���4K	�������m老)pJ�f\C��n�Y�`��;tn�L?�������u�^������+�V>o5M+?I�F~�P>u|�d#H�na��r|H�,�O�/��6���=�ѹ��r�*�-�x����!�����&G����"��,���O��3��>�PK   }y�XXW1�  *  *   org/apache/maven/wrapper/Installer$1.class�UISA��$$�Q�QԈ��w��X��@��lghB�0��� ��'V��Ej�KyPO�Q��;!@����^��}�_�y��ǧ� �b,�D√���!��e�W�� �%�,\��+�[y7��?3��.� s���rH�a�h��px�/s˕�5/a�ʥ�#nҲ(z>��HW����l�@
q�M�@'�΍��/ �9�P0��+l4���B.[Lb���mb�2toF��wDPuB�������Խ	
��[�\�:7��ك&�q��/������ar�Pk2�'VlQ	���MbP\=&��(CrY)�C�7C_������@��slU4�f8ѷ�"����E�Q����.����T����N��R��¨*2��m�g�)��m.xs3��KT>�F�$�Ӯ��@J�b3tm�p��ِ�n�J=���^�Ín��{L����(�����p�lM��T�� ���]|rV�]V}:�B��zd�K%c֫���l�q�'�9�ʄk;^ �r�v��p�yE����E��n�20�+�q���C�L�z K�U�U��6#y�V�4��l������ڵ�8��`4>A/��kV8�>@�bZ�U�d�8MjuXN�ͩ��h��������$݉(�Oi|F�='�����ՠI�:�1D�'53`�"J�S�RG�O։��NY;��O�|^��J4�8]�J��3Z��ct�sd�������}ž��8�Ʊ>c�^�fl-Z��$���z	���
��@��v��Kz���6�[����	=?PK   }y�X[/A�  �#  (   org/apache/maven/wrapper/Installer.class�Z`Tՙ��d�;3��Bȃ"�Y�	IPz��$�����`�[�V��[��Jڊ�<d�nkպ�v������vw����]�[�߹��$���*s�=�?���������z�E ��y����Q�B��}��&d��k���gvڂµ�p�^'�/_��^E���hw�1:{̚^������i
�l#2�w���,# 8�9熆$��S�L�13���v�Ź7�'����:J0[0-b�=���ٻב�<�����u^�a���:|8CP�sێ�s��
vǣ��ݦ������xbff��fǰaӮپ��9�,�0���X}���5��DK�T$~����0�د��X��s���c�:oMk�W0;u���V���y�2�kX���9|<�@�{#��U�ְBY�F�tj�����V�|�~Y�lu�Le��J��p���8Op���H�w#C��,��U��)g��O��c����["���VC����PP2�N1�	<a�n����*�vȮ�\�-ߩ��s��l}<b�x��n��Of������e���AG#.fv+��s���eb�$�r�QG��z�lF��	�6�hq(&�ɠ����1+�M5��ڪ��l�,	�T�uh�Ѧ��m[�E/ڱC��::�S� gd4[��*l]�p�%(�f�v7v�T�*�*�ԱG�WD(������`EĈڊ�SG ���Q��0[�{CN���6��xPaխ�Ar	�!�6��R!4'k-�Y��i�U0��^<�%8������5D&$�+2��Q��i�u�q@�K�j�����5nm��и���ֶ��KXU�?j�c�~8H��Zh⎬��J�e���O���J�B�1��u�Z��z�\�3z�civ$&�!�s̮+
�o�GǮ�qC0��5�*X��Ѝ�	�뫹���:���(�SH�'�Sf4Nefy�R�n��^"h��t+����jM�y*~>��~��S<�������V��u��tq��+Z��ѶAe�J�@0�Y+:�W>zXǗ�q7�9�RΤa��u`{�`0Q��x�'�m��tT���u�v\*�n*g�)8cT�m��5�;�d�<�oi����p,U�A�*�۵m˖6�y��FpE�_vvl�?`�1?k���
�F0�7��&Z�o�ph��]�p�o��vh��2�J����èˊ��Ŧ�L�]Sr&�70�:Н*����s��.��� �)�ӕGN�����ܘɚjv�m��}���b�@L1�����%0<h:�U�/v�X�P1Z���f�+hƦh0�� �<�Ѩ1��ۋW����/u����C�f_Cj�al�ΛEғ�1�~��5��`ޤ���7t��B��`,��������Oh&#WS�۩�yK�?��BAgȊ�~�R�!m�Z}�^��?��'���8Y��#I�s3��w0Y�k��
 <���ꋹ񯄆��pM��o�����Z����羅��G��^����?u��T�5�a'�N�Z�`�*�qC�Z���xq�R�x�մ��{���!�0���`�H���z�o�Ĝoڒ�7�OP����N��l�d*�������Z��{�sW���L#f�vO0�=Άq���Xq��U��I�d�
�P}��C�M���(O>M�[�a�=^��$_���V�l�7�$m�Z�-��w���=)�.n��y�[	D�եHx�,��f��w	�1�/���s�t�ܗ)�m�.����+���33E��cD[ͫ�f��T���^��/�KVz�D�i2G�l�Qz���O�3�L6�}Fh�
���I��Rmy{0T�6k1od�.�U�ʛ�<DT���\5�[M�*�6,�e�,!��V�v��͒�����Y�t]&�U>v�Zqu�.RIӛa[��7�Ah�{��u�H�e�&5��� �4+o�l6[Q�1d��kLm[�˹�](R�BrA*[6A�F
2���	]�W7��x�r�.u����7Q�d�hra���a��C��lg��pg(`�5���Y$�i�~��ň:t�����]w��0���l��΅Wʨ��70M�\*�h�N���ᤊ�`۔n�9�����ͺ��6���)�۩�U���������
5�2]�I+1��]�������+��&홨9ʏZ�-�t(0H$�[v����^��+5��6D>9�"���i� خ��6VpܲW�Na�54^\���mOo?ۚ�ook�Ҳgk}�F���6b��I�W�)|-����4�k�`��h�͖x�^3ڦ*��IܶѠzON�TU!TN��g�؍��}��GY��{#�/:���O�����c�D�3����`쥙Ƹ�y���Xc���f�&~����?��t�a���YՇi���>8wؙ�o��ϱ.��ܼt��l�XG�x��ܿو85��BeڍB��4���c���
e�r4�P��,�������9����Æ��ĵS���dU���J������KG1o��S8��h��Fw��^�jtۺ���<i��HTh�T�K�)3Qˉ�h�3�;NY�4+4S9����υ��DI՘ �����ԃ��3���N0��۷��Vb���]�����\2)��v����m���N3q��48�P�i��p�`2�f��	I�����p�����b�W$�sf:����ZLL�t�-�].>gWA**��VQ��UT?�Y�8�'��-���b�P�0k��B��lEb�e�?��s����:;�g���Nο��L��A�n�ϙ���`������4�Q���T<�Y���-��Zm��.u��������Xt�:�pQq� 6(�KFp�`[�m��׸�ݱ/�cwT�	}Zrާ��
�C�'I����C���F�J�U�<#ؗ�ǧ�^�Ց�4�jFߎ�9W�q;��z*|�!\S�-uA��~��;�a�^Zped8ͧ�\|���>[ Eq���W4�C�t�}A�l��S
|Q���շ§ᡣ�_M}Ei)��Gիg�����)Q��u��xO?M-�:�}*���OT���#x>O	4�Վ�`z<<����d�xJE�aa/b���7��F��%��F�¥m�v4���.lA[1��p��Z1�6|�o��st�m���{��Ӎn\)%�+e|������ގ.ٍnك������'G�_EH^@T^�-?A\~��N���x=��e~y�a��9�U���#K"N��A��(��+���،����s��B9)8*@��H���X%�� �i���oK�\�N�oe�|J>Mn���_��k9sg�`�)���z���o�Vj�4����������9����P�>���Xnt0C�&Jz�G��Mi�[!W����6W&��0�*��ZK���q��q�L��qY�+H���0�*_�~��GO�Y�2�q���b~�����W��\��A���:Q�ϟ���I��>>qR�� �Q���)ב�z�~��FF���sw]�����j�xJ�Z���W�g�V�����߱1���y����m�O�]�5e�s�������CZ��=�5k8y��X�)oJ��=q��GnL��2��~*I�@��@�χ�:VY�/#��<l.~g�n����8�%-������t>�1Q�x�Ϙ�y��ݤі2��;ZwR���z��^��O�CL��P����B�C�����]��֧�^�v6+����A��y/�H�i�ݧ�o�&��5��S�_b~R�f�95��԰�i)Ose�L�O�+�ס9�Ct�,K�I���FsH� y�{{���bTQM�<.%CR:,sw��af��3xNO�9]>��u�|_Opϻ�sE\y�jD����#�P0$g���r�
#R��q5Z��յ�լ	��q���V�i�#rA5\|\�v$}\"��L�$�;$�P���Qx��c�s�����xX6�`q����s;�������/c��=(m*�<�uv�U'�{|^�t�GO�+�b>���Hg�-P��g��Ǩ�!��	�3�q>������x?M'iSx���)�e�އ�6#���	������	\���v�D��'���%��_ë��~�8���>��r�<�>���F �����Tȃ��2�!_�/9��Nڝ��CNԪ��*j���e�������[����Oa��\Y�I��ئ�j�zO����$�:M�X�x��$x'!J��i��	��%#������4�
m�H�*�ic������:�Kdg��~F����б�Jr�����cN��ǹ*y������PK   }y�X;n4GR  %  %   org/apache/maven/wrapper/Logger.class�S�NA��-l�]n�� r�Ee���ŀ!�Ú6�3�aYlw���Q|0Qb��C�nT(�Ϝ9�������/ �WATALCzw�>7jܵ���]Q�z������l)�8
TIhS�����a���p��!ӳm!�������6�ǀ�AC���>7}Q'
o�Sf�q<�t\����\#��Zqu����漫�
�
F5�a�a��#R��m�|����)j�*��vsٮ܁�	��쨐2�)��J��Fq���%G�4C�ҁ��r
�q�e�
�b�C{D*��kX��m_��]dg/�Td��আ[��0tV��y5�]�jp��n��&C��Q0��+�{�-!��Etӫ�Z�K'������4���,B�Jwۻ��Dc�7��B����y�S�7NH����j������ɪXu�t�E� �.-l�V�a<XY���N�N�yYF�g�;�!]"B�Fq�N�U@P��&p��M�uRw�F�Ч�~"�.�h}C��EC�~���O"���Q<�#���Z�&�'�/������C���a3�f;`�6lOO{�3T��c���P�ٜ�<F�;�PK   }y�Xb`3�N  ,  /   org/apache/maven/wrapper/MavenWrapperMain.class�Y	xT��o�&�	F�d�	�"	H��	d3��5>&/��̼a�M�V[�.jkKWk���E�I0E즭ݭ����k�ͶT���&��d&�������{�9�Y���=�"�ω�*�S����q�6��Gk����@~} 0w�W�;�@���B�Ft�V�h�1�6������Q-ѣ�r4`:�@X���ګEJ�+ڭ���v$�k�5s�N�_�b	��&����f@�	x�3��*ʥ�D�R��a6���LE)�,N��ף��A�ΛQ�:�ό£u
������Nq+���j�e��֩݉c���k�%)<,P]>��'v��"֩X/5v��=��FlR�YE9*�fuB�1:���y��A-3mM�V�JA����A��h��CE����3����`��e�-*�b���-�m�,-Q#������ۡE(p;.Vp����.ʴ+m������<m��:��%P��+D�f�W^�=
T4�I�<������qӠ�W�сt�P���, >W��W`]V��p�ԂA=��r�KV�f}D��f�p8hhD�m�Ih<|؉��]`s�d�N�����j�����c!\`$ ��Cf�+6e](����A݊^��^�x�,���m5B���*�r�/�_����f�=x]v�M`���e�����C1��}�5屮D��5*�p-��n9�h	d����$�� O&,�4d]�j�#�����p{ �7�q�>�&}�/)+em�L\8,��aLE 
�Fx$0�Rֶ�Q����!,�>4�����]��q��|���,V
��8�"+�	x3�%[�S��ɣ�*�����9������I�$vg�8�x��x)�%�&�th!��
^�����t~���T��q��<{K/�7�����\��D� k塂�|��������@�U*^-�F��cZT��z��;%��T��;��.��xk�U5��^O�#9qoT�&o�[�VK�jT�5�������B瓤.oSq�����}�A���P�N�»�̮m
j��m1�ʤ�	�f#du`�f�v�����hm:��^�O��U| X�0��4úψG�D��y[��R���aQ�Q|�e2��vï�VgV�lu������>�O*8��>�O��N22M���m޲6F§V��|Gæv�{į[YA����Yk���f �'�xS����MP��zǢ�a�5��ǁiVR�H'N�a�U<"��T5�l4��|�gU|�'��Cq-Kk��\qe�c
���Kx|~إ�d��������mO��s�+���p�0��:�u�)�헐�0v�l[܉`t�Kw��Ys��ZLwo��\#U�����T��!��W>��xޟ�<󹤉��&�p�I|[�wT<����Vʥe/9����c�����6T�u�$��Q���j�|8���Q��(b��$�{�?ď�P�atJ�6�x
]c�\�=;R��0;V����1[�Y�6�Q-��l�ρ�
t�(����G�xx��|O�;W>~��W�5$��Y|��+��,���}m��S�;���D�>ʦ��9����4�	��XD�ˮk��_$o�Q�W��y��ŪØ�_�Q��w��MbG��[�<	&'��?�Kſ�}�$����A#�+����$���ee�i�H�Y� K�"rRev�D�1��xp�=7�g:a_:�A�� ��u:Km���D�m�8{��1���sAU�t4�{;��|ޞ�֮/g�bU,���'.��xb�*\��w�foKC_{�P�2^�2�O
�۟�ut�~�LKb�nR}�X�R�J twuu�tu{{zۼ�d�LIg��e�6��`\V�4�g$����<}���oϨ��C�ޞ�.��!�mQ$kgCI�fH�>�@WO�Cl�Y����b�s�M�VY6i��u�C�h��<�L��Z4 �	b�9 �+�������[�U�K36���׬}I�i6�v.����J3���SN�Z{���,�L�GL;��Zrƾiط�����s�qߥ�������A�|�ѰfZW����kI��4��)�L�V��N��ΟCL�d�8���	T�i!գ��yqY�r2�ޕ����v�xt�m���e����q��\)V���|�r�4�L9�.kf����ڔq!����K��xC�x#ǛRƛᄐ���RNJ-G��X䙄��b��3�"���� *E��ZԐ��E
�����^9�L����)��3o+'�ҵj���1���a
����y�p�.���Ϊ)�ޙ_��F��4Z]�ey��;�
�F��z&�7��i\I�U�zVՋQ�g=����b�A	�AO׌h�l+ц!t��;�n�h����V\�w�
�B�W�-�ʡ4%�ʥ�kQJK�I�b��F+6b��Hlg��Z/�%��Vl��{r.� ���Qq�4��騬��(�]7PY噀1��#8|"y�R
|�I/�чe��JXJ�m�	�X+.;�t1��:QO	�,9��J=�b;z���7�����I�,S��n �3�p_�"���j��*Z�l�P
06Z�7+�
�fu	T-���+f�'�v E�R��RВ��&iTJz�rK�k]wO�'�֜yGR�%J�%��D�y]�	�}���	||�q�9VNb��!�����=X%����/�B�`��7��N⛧i�o���Y@��D���"DÅ�!�yb�:DE�&�	�=8�����L�ᨄ� )y5*l�h"��=
8#�[��z
.�r�Lb��G�k\����>�c(��3���L�g������y{%��J�o&�ێ*��𧁪NϣPH�G��v~���(���B��}��%"�3)��"{��F�T���WA��;7%"�'���˩�X���73*o��J9�q�\y=�ކ�n��]��͌/�Q��Y�\��^�B��4��	��#�J<�6(خ�����#M>Μ�r8�5^t���Jfl�+ma�b�D8��S�Y"
�D��,�_G�'��M��$��ú,���Dr'7��㻓Tx�^������^�労�hMf�K-L�%b9�NlR"ʬ�|@ˀ�~����+���
���PK   }y�X���|�    >   org/apache/maven/wrapper/PathAssembler$LocalDistribution.class�R]kA=w��vۤ��֪� %I����R������mv3M�lv���-��?�?�xg�m�A|�g��ޙ�����+�xpPrQ�QA��8"�E2
އ�2�	�#����Pj�5,������:գ@LE4��D\�$���t*u�A��Y&'a,��~����\�p��4!�CN{J��V4Qip�bi��Fh�Gݨ��/jJ�o�a���M��P�D��&�ԟ�Ch�yB+�/�r>V�M�w:��#����6Z�		W�Z'��{ә��<iޒzi��]�Hݍ㙋�����voX<�v�8h�[�iޔ}	ğ��v���惰�w~�:��p;�X���Z��`����#�s����iO62��x��	؛�J��o7ͫ�m�9��hH�,$��%�
���}�m��-�	���PK   }y�X\�@j#  �  ,   org/apache/maven/wrapper/PathAssembler.class�VYW�F�$�X�$��iK��p�f�l�%8eR��T�+ؒ#�@��������+=�š9m��W�zzzGR��vO�iF�{���e�̟���;�����
xx%T���ᒼ GR�6�_Rf,�����Z�<���j���P��E7�"rF�I*����h�EC�d#2*[�n�T��b0��'L���
ÖAې��Y5���]>ԡ^@��M�3��?�=�6��e�Ru��~N�zU�2�x�Yi��v+21%"�H؊ C���MeX�v�K��v$b����wIh�6�e(i}A�[�ʹm��5ʀl�p�%��c�ap�������6K�;6o���,Ct�����sCE\��-6�ЊP�a.��T�A�tH�`��X�A0SO-�P�FYF�$<�C�}:�f�[|���������9�ÜC��N�÷W��9U�Gp��c�dK:0�3pB�I^���ͤ�S�6�z�'�_�iP���Lھ��U"{n;�g�A	Cf�
7��$~=zB�;EԇQ�0&!�q�Mk&�����Pc�ʒc�ak0Z�EL2���)�'��lZQ-�,��r��\�	�(C������@0Z�
��1	2/�[韑��U�{E?Ł�T�>+a�ˊ�� �P�)uO�OO��ƦF��8�y	)���*��r�����]��E�dytl�L_ϸ����fّPLX�x�)L��R��QC�R��Xbض&3��,5M�jF��b�8C�� �ʪ��B���#�)���Eg3_xF³x��x�k	�˷��\HJ�İY5��ڼ�/jsևE����+e2HYl�tl|,:|�B�<�0�
MK֬I9���s����:�Y?�j�p6W�q�6,��;vR6T��.z��J�
����v=h,݇ȑ��@)���o�ၘY,F��8�^<,�	w���'�=�&I����m�╱�r��S��5[ٓЩ�0j�1K���3n�m�	#E�q�4���1���F�Y�o���4bMm�T(G���@��H��C���x��W���F��F]dh.�݀��&}Ehd4V�r`?٢oѻ�^܉��-9x��ȕ�s��I��q�P�kuD	t�zтj춑����g56:y��.�_�!�x1^Am��Bm94���9������尓�ݫW��k����^����?m�8D��n��TSeSU]�y�D�C����Hgf3]C8@+щCt���yg鋻1�̻q���_�q|H1� �a|D3���H"��R7�����'�T�$]�\����˃j�?>����)O{�z��� 'k'l6!G+�&`[f��� �Z\��T�3ֵw�F�$R��70�f�I~OA��<��O�I�C)I�<ف�b�m�)�پ�?�^��s��i�ހ���h0��f.P���u�����[�I�Θ
~�u��q�/@��=)��*����ћ�o
-~�zC�?�*œK����|�����������܀��[��ũ��2���'W�4�<��^+x1�������.
Ք�:�Q]İ�z�z��=q��J�������aT�"�G��PK   }y�XR(��  c  6   org/apache/maven/wrapper/SystemPropertiesHandler.class�ViWW~.$�Q4�-�$,q��*�J�Ģ�vH��H2'���n�M�jw>�� ��S{�������ԾwBHb��_��y���˽����g [0�B*%�d��`XzB9�������a��ѣj������3*�ɰ�J�jܷ_I�����RͰ��i��;�ƹ/�j�IS�5j�DB���X�P_,��>)�55̣�bæ�@�ܐb��G�s���Pϰ<ᐡ'�a�<�B���$��$��A����*�%��גi3d\I0�[T0yc~O�E�;~F��5���ו��VV�3�B3�J�GF��N}){8����u9+}#1C�T��܅V�+��W�DB�d��/p\��yn/t7�w:̭CI�dX�WNk���|Ê<��T��޴�pÅ.T:�����0t�y2Ƶf��4'2�,��)���؊mT�J�p��;#���\��/L�&�E3[����у�NS�2���JUH������-�m0��WF/ \3���~[�sa�/���؇%�я�(�E\�UMn(�n04�����@ �21�PW*� ŔT��6�>?,4�e�0BE�Y���(a4W'�=��}�(Uxc�8��t#�S�J8�}	��|��H��M�L��w�~E��9T�32�7dp���Q~Zt @��c2��pQn�Sb�Qf����q<W���t��bRl�vsa{c��'�\s����@V��ĠJ8!cB�[�f g�v\�"B:!C�N�5�t�����la��!!%�D�h8����ZԌYI�(L�8-Pp
�J<ͫ���𔌧�LQ�P�H�	Î2�q�n�'9�ŕZ��Ѯ�.�G�����!�Ik�ߡuˤ����#�VK�Ӊqn��Ρ��a%>����'�̘J7Ȧ���4�TO`��eXWv�FL�E�i�Q�Pv�30^�ˏS�UN�)q�l�|���wї��h�]^���rSVԖ�ʕ,�������/E��J���/^�b�l��Z��sǙ:Oz5!S	O�����aWtz����3mP���EG��	���K���6�<����T_��� �k7z�
�˄�݂ki�V�;�n\1FiWI{z���e����h�{g�.Ӧ���a[�*}� ^��V��IR�\Q�ڀ?� �͠*��	������~iuD\> �W�"�љ���_�B���h��O}�Gmn{�ݶk�hΡcl]daC��6{3؞Aw�v]��`�,����8�p��<��vۮ��6�G2x<�,���Y2m�e�v�$ϣ�����,�YLU���͋���h�6z��`7]�����0�t`O��m��y�@��t�V��H���7���.�бg�|���Ľ���\����0Y[O��"�J�j��x�v���=J����>��vRTP4>>�G�:�>�'��5��O	�
�#z#�Y��H>+�9I}A� \7� ቛ�J�I8O[	u�1	�7)��B*�,��OT��m��*	��1�y��*��������**��g���p�U��[G*�X���;Z]D=H����PK   }y�X��   a  '   org/apache/maven/wrapper/Verifier.class�P�N�@}.%W
�&F6�$,�*�UAY�#rӫ�%:�~���.:�0X����������a��6�I��L�N?yS��u��N,{�:�=3�ѹq���8�N�O�ƽ������?$l��8x�.�u^��`\HL>��J���%D���0���:[��Yߘ0����� !4/�S.�o�M�Β��V\�}ʗb(���|��F����UD��T54@�&���.�%���mPK   }y�X�W!  �
  3   org/apache/maven/wrapper/WrapperConfiguration.class���V�F��!��U �s	9PJ�ӠNM	�����>P�&� ��eɰ�3���*+Y�����G�-���G{f��g������ X��at�N�
���0�_p���5{�V?t�_��3܉͖B�axj�NT~�Ou��_�zY���zM���M�<6N�5��ك0�ĎVJf*{�Z.�ܭh�=m?_)fr���0��OA?Z�W�������L,ݴ���du��TQ�0�`#���Uչɠ��7�aY�r/BS0�O)�W/��]4i߿0�r�1LJ����e�Y-!��+x �����uiV-~�#�N[!s�9�l[?;��|���U*��vv')r�`�I�#�&惺�{��:C��~z1�xO|��Sm��L��U*�0�Hni�t��H����b!��TrZa�a�+a�zlTu�yU`�
�Z�ĪX|�`A0�~5���[R�,�܍�W
��kҽ9	������r˽P�&27{�O����|��'��m�d��p�Z�6i:%^��\����:7�#Z�O�������Z��Z�5C��d�sj�j�]�U�ɰ���p�Z��m�	�t�ʳ��wsӐ���ݚD�x��V���V�H��GT��zl(&�{�l˜�7L�Z�?�H����n'��Gc2o�>L��^��a7ފ��_� RS/��m��y�^;Է�㲣��xD-�A�o�ޕu<�7du���o��	����~?�G��H��隹�o/���kG(����uE�ƕ�;�x��=>y��5F�щ���xt���?�Ɯk�{��n���O �u�!0�{�3��x]��۲I��W
	$id��w>�J�X�����I������F�6R~�:y���)��.Q̲�a���3�/��(��d4�\
�R2Y���6 �>H;��� �$��-�'�X	�$)f���%Q8�W�� HJ
���d:�C1�6 3n�3P���A^IAJؓ��A�Sj"n�(��T�� Ⱦd_��|�5żi��+R�*��R��,At�9n��+�T�� �!y-Wd5rF1f�5_�7nL�PK   }y�X��e��    .   org/apache/maven/wrapper/WrapperExecutor.class�X`��~[���e�8�0D��BH�;�DX�����g�,	�d'@�-�{7-]��J-��(�@K-�t����~��$K� ���o�����#�}�^ ��U
��x��2F��-Ƥ�h�J���n��w�eX	>��a�{;;��#Y;��c��ɥ[�Q�7Z�J'��-33~�j�]!x�1i�$�d˘7[z{���63���I�QgC�H���촕om�ΥiհP�(g!k[�ނ� `��%*��h%,���p�` �8J��Õ�F+�km԰Lay)A�J��I�V2�
�a��cp�B]龭|djI�c��������]��	hmة�x��g]ӗM�քٱo�t��bN�p���8Ia�!���g}}==���|�Տ5
M^�C����5'eքF�҂�)&��А��hӳ�Rs� �&�:֢�d�$�1�LO����\5�~W�\D>�wpN���S���>|�5�ΰ�P�oI&Ƭ�l�p��k�5g�8gQ�gY�:�F��d����T�zXai唓���h�f����%�����G��]G�d�<'J���tl�|_O��5R��ulG�*��f�H��I[����@�b ���3햁��V�G���*,ʘv1�p��A?��e�h�f#c
�~d���r�u��j\�c;��3f���~X)e��X2�.3W?���W��c7F������pE�$4u���~;�*�X.ձ�9�!t̒j�Qh�`�	��tBG����J/�JĴ�~�����| mY�#@�vZ����}��@�0���E�y^���@�>?�*�=z���5��� ���N��N��Z��u\#r���V��:jaħ�����vo-ހ7jx��7�-��Y?nN&�������u�a�9�t�`���:ށw�9k(ch+"�N���:ރ���V�!:�S��>���i{r*!�+d7����-)��W5|(�8��¡��G�[����޲94U���	��
�~T��&��#�U��tr��M���8n��	��-�ȿ?c��$��LL�k8�_^��q� Ԃ�i�:QM��܎;4ܩ�.I@�sx�RN��<aJ�}V�ݒe2�K�K�B=�cFH�{c)s��F�����8$�klJ����r��w�u���EE��t|A��:�r�}Q���+�1�hb����!VW����V|�L� ���H;=4b���bI!:�J'�=4i�-�xH����k�:�K8��-͑=��m��G�-���T��=Ԣ��q�8�cI?���Z��'t|O��O2;,)�~���5���q�z�S~���Ñ���x-��pgg��ߎ_����Is�'H�k�V�����:~�ߋ��澞�Ô���E�?Ɏ�r#����:��i?b�~���-9��k�w��CR�ƸJ�G�O�����tB�9Տ����H"=-��-�7���a`g[�gu<��	s�3���T�4X[[_T;9�,�\)�C�(��*UM�*�O��N��Q�4���/mv]�]���x2cj*P�N��Z��j�b#���eS�D2�Hp�Y��j�T碲�7���Q����R�~���fsg"c�$t`���N��~�m]�V�PGkj���Q����YaS{#%��M��j2�%�[�l�|Dˍ�B��
�j����K��.+�!U�S�ں�đx�M�\C{��n��q!���t�tF��kV�w���:7�w�t��{�zz;���
��b�^��f%�A�̋L	��m��"�vv���{�:�WJ�z���U&+[Y^B��6̞`86��j��h��������SUof���m�vf.�,t�\>ڭ4��4q������O�M���$G�E���ى�f�_r\<(��A�I�4x:� ��1�-�,`���2R��o}�Bo �{,6��/E�j&s��Yr�;b21��|#�33�lz�B�^hAAtFS�֬,^��'��+��"��H}E���b��)�]L\(�S�S�/����ab:f��b)�0�aO��g%3(Fz�A�� �9��
�_�xޤvWĘ#\_+*�#�ٵ-9aJ��J��G�1#��vͤ�ĜL�e��V��*�~a �B�V'A��9��S�)_�q�h|�E�K8n,��8R4��㦢������"(Ws~���y��{j�Z��pԆ�e�P=��9���₡i,�F��L���<g�&��O�%Q�9�k���4;MY;���,���Xy�݅P�d�=U7�f�8�S�s$���vD��s}؎cx�?��=͵G�.��N�k	�]�� ��p��'�w�e�Mw��1r76E\Ŷ�c��lm�,W�љC���@^9Ħ�#�]��%����9�#2��&������C3���w箘C�Jw������׹�k���9��S3x[�rg�ξ�f۝��]Fğ�Ӹ�;��x?�=O^�e|�O�l�"l�Q�����4��W��}��g�{Џ+1��� ���؁[q!�g;�.&��w�O�����Fr�!�o�z�A׫3ԙ�O:�RΗ�b����z���5��ט�J�W��^�I�*�k豏���nG�RwCAX�:Kҟ��id����i�f�[��>�0Vұ��3�s�9�~�h~>�{����M9��>\}2}~9�G8�Hߘ�7������X�T����Fs�qy���9��~��W��݄`���z_����7�<���������!�>�-��fwaO��-�n��9Fk�Y�Rz`/��q��ŀ&��'�)ZC���Y�L�k
WDn�`:\OncL�a������V����M�lΉg<���a�M��pj��j�?��"�b�j��̂s���6�ߖΐ9��������1�,�WU�BWp������� �K`O�����4��߾�����A�{�Y�������
�B-R�Z���ķ���k�z-ׯ��נ�;��Z����v�)�x�*_�׮SZ�{�d^Rk������b��'�Y�i�M��/�/���|q�߁���I�(r�ߋ�R�m}�G_#���7��;���_�T�lr�麈�n��N�*�0��T"U��j��:R��Ku;^��K� վ����@����g@+��~�1��{�&|�����@M�y�?D�rM������U������ȴ:�����$������B���k$.���D�W�)� �~)'�<��A�ꮒ0:J�s^Ty�]�N��`{�'P�?�̼���PA����R����PK   }y�X��   T  ?   org/apache/maven/wrapper/cli/AbstractCommandLineConverter.class�UaO�P=�ut�!���QPa�HP��$,K�fW�(�ڥ�@�����D�?�x_[�����@���}�{�}���� �c[BB"�(��g9��4��Y�c���]���i��6CH��DD�m��jeM?1ԒvfX깣�ˆ��ESݵK%�:Θ�q�9Ñ "$aQCQ��0�fG��|�u4�m�ڵ�3�q�a@��/f��kr�71�x���(0��<˰'g�;�'-_4R����qE�w�0�qL0��/���"[q��s�E1����q�oU\��I��|K�4W2Ð뙿���Y��n�o8@a�>&��<}P-�'��f�غV�i��߃����T�:�L�a�&�E�����Mg��mo��è�)P/c�6�M�Z�u�ID�tC��uL����Ս�k�VE��]w��l�)TK�����]<2��zw1ٵ�89wg��Sɰ��M��QWSN��ҩ.�6���^���}Maُ��٭V���T�C�2�ґ]utc��?�i��9�0G�<����a��i����Pd<�\�}�E������xJϨ_�gxNq�Ff�"]Fȴ��]PQ��	��>(�$%�T�Hc�u�{^�"��f<�Yk@�Wq(�0���8�8E�j�^���eZU�Ҫ/<a��C�|��,)5�j��ljv���Ku�����mKs3ߺǷ�}�(P�F(InD�����;�@].���B���U�'��%^[#�'!O��\��I,�įM��j7�z(�oџ�J!��7PK   }y�Xm�v��  -  I   org/apache/maven/wrapper/cli/AbstractPropertiesCommandLineConverter.class�V[WU�N3a�0���Jm��D���Z����p�� m����83����껯��Z]�����A��}&�-��M�b�9{����߾����0��%��O� Ï&��Y�+e%���Kʦ�ǷL�\V�x��ŧV,�Tr��Q*)z>��괡o����M���ٷ|�HVD��}]�T�EE/�3���	ZD�2Zq�a�D�L�D��Z��v��=;�e[3t��H��x�[o���Mw{ͨ��ռ��pADHF'���x тbZ��!~��I�T_���+"zd\ī���˄��XSf�RRu�b����d�r�.6��ʙ�3�P_�qWΓ��f��"�)ގ����wkmV)K�OB"�<n��r��?`����z��16���!oa�a�,J���R����n>;n�5�N�P����q׉�ïU�b����N�\9i� Q:)�&�l��-"�qNݶ��~�O�#c
������u5g'�a`7%��#�]�%E��W��WB��Np�-HcVĜ�y,0��C�0�r�L^{-z%`q<���>�kUx!㜟T�a����9��;$,�x��-����-[�s�M�('��ޏe,�"#Wk_�S�X�Nm�
̴�'m|�\�������"I�i#�����+�5�,y4���b2�L��j��I�G����<�,�ϲ�H�N{�4G�J%���+��Z}�&�!�Jn%xbD"K��S����Z�S�A�)J�ڇ�:�7��^$x��s��p�wʻ�;���[�m�	W���Ω5�"v��8���-�;���?��	�8kl�g�wL3��Ѭj�y:��T*��y�2lT|�gr�;(e���S�h<�ѓ�� �%\�?�f0~���cZ�i�n��3��ir
=�aV�)�6 �< �B����Gڣb�UZ��>����!.��T�Km�*���*^����R���!��
�p��P}��w�ִ�8���V��
��i�wv��a9��O�[z�7���*r����s�ٳA2��d���͡	X�r�U��x�9F�h����4y���	�[NW����O��e�9��a�[����W�~������}��1�Np�]���>�GA�F�N2E�%���ԣ�iQQ�Y�uk�{�,��:�-ÄE~�{�rI�$�4A�"&��W ����?�Ֆ�a�%��"��'_�������>E6?w��GR��^ ]ţ�4H�G�����KY������K|�O�PK   }y�X�:�dP  g  ?   org/apache/maven/wrapper/cli/CommandLineArgumentException.class��OKAƟI�M�,K�(�[i4�.a!��!�������2��}�NB�>@*�Y���C30�����3������%l g#_F���2�3���T&"��.�J�ZH�t	j�η��(!���Р6�H�Z�X�&*����u��-��Py�E̝p�)�t�XqE]_�^L�!���Ҁ�de�|/s��I?�����AT��e��)a�e0�LDL�q�ݱC`<�����uO����X'kk�LPꇩr��05��ua h¼�Y ��Y�^W�&n��s��,��υ�X+O����B�
v3��=T5ð�K�=rzv�}>����E��XȾ`�f�}d#ֲ��'PK   }y�Xlk�I  �  7   org/apache/maven/wrapper/cli/CommandLineConverter.class�R�J�0>�������7�.4�
���L��в��;֌6-YW}6/| J�R���z��|�~Nr>��? �-(8KdHYʂ�1�Q�W��%"N�I31p��n"r�J�C�31�������m�~P֏�i��9���[�i�1��F�z�872�(�)�@��`�X"�cS��L��$��q���r�����8]�Ly~���g�OL�p0ӯ��rym�[���U\--<�����g��;�GfF@+J��Ss��[�:ƹ��G���4�'V� ��|5��B	,u*��*�ծkY7 �4 [i�m������=����7PK   }y�X��I�U  �  4   org/apache/maven/wrapper/cli/CommandLineOption.class�VIsG�ڒ=�4^P�؃	K�`dC����,�����IFR#��(�K�?�%��KI*6E�J�r�?����T*���-���p����{�����?���W ��u=�)��E���XK�YL.�6y�a��Mݙf�Ec�
�\���ɛZe#˝ ����`@� �NYv1����O����L>��r��ɼ�'�V�����n�Ų�[&�b��
�P���S�؅���T�`7�`�䲥�K����L�"mh�JJPڣb��R5�X-q�YzV�!��U�����m-gp��.��e3D�1�9)O�0�#
>Pq�vP�T(�����:n�ք�	"J�MW<����zJA��Y���b'1���$���D�%cw�.����N�\��^qDvΩ��y�`����jB�dZ=������Vu��� .�ʟR1%�B^�ۺ[Oq9��2�P/�Ƣ�.bm"	�񪊴�x��yM@�h�Ij��5�q�<�,:n���Ŝp�1��h��� 2���;м0:6�[�o���O(I����ȵY��� ����7���ᲊܣ�q�����p��z�Ԝ�M�v�������b��ç�B�Ǜ͝NŖ���V����9[��r�^�5w8c�5cY�uq��>K�����i�3�6�-�Lh����J���w	~ �h�G�ZY�&����D�ȝŝfW�<�'��x����|Jΐ�;'I�@������pI+W�,�h�㼼�c�8H>gCJ��y�jPD��N	Y�&�0��������p����=��Ɛ�[�h�gR�ZU;ϯ�Α�HN	�8B��C_�>���t���ы_?ߧ?z����\�r���u�t{�)����;|��z9�,���K�o!�	�[�$^a���W8��hs��7��?#8�
���x�׏���}���@�N���aD�8H�s>�1|F���t������sh��ny"X��JkM�i������`�R�_!�M?�ȋ��]ƕ��0�<p���q�E�W���l��� �I"t�p��o��b�#�	V@b1�:a��q�0&;b��'1���mf�C0���2iGJ<���m���Qڅ����P����:�?%��)3�h�ԄX�qi�֙7�&�1�����m�\I�o�;�4�n��Rw�9���f]�m�T}.�Ij�%�&5�u�-j�N`�Zj�
q���g���%��T�j�UOh\��P*pdS�?��`��#���j��M�en�2��K�d���&�`����p}��~��� k��y�9,����" ��!g%䐀����C�P3​��s�3mo�ͣ.�e�w�?!�)q���,+{�)��}ֱ�gZ����_�Z_�PK   }y�X�#�ر     6   org/apache/maven/wrapper/cli/CommandLineParser$1.class��A��@D�k4�f5�Y��V�†(x�?�OL�t�nu�6��V����^�
�r�?��#E�0q�Pܰދ��$V�yn�J�R�]]��W��-� �{�"!d�X����V����������e8�/���ƅ�k9�]��O���0\Z+~n8	�m<�F'��.��	z��G(:*�� PK   }y�X�@Ƀ  �  I   org/apache/maven/wrapper/cli/CommandLineParser$AfterFirstSubCommand.class�VmSW~N,,�	T���=
$��[���T	��>^�mXMv3�E�#����ؖ�N�u�����I�XR��v��ᾜ��ܳ���~��g g�$-bHP
����u톺���~��M7���g��FC�;e�w7U�r�L���W��U���l��e���e�3S�g��z���N�Va��3�0���]����q���4c�k1!�4#/��*�R���\)��[��[��xF�r���\�3�blg�w�=e�E^�^W��(N`���N���+\�@͍gf:Zu�����׉��%x7FBx�'��o��~��w,�`�O��#���R�o��p��8�Ot�g�{ӯF���b�c�s��d[w��o�L��]닪�A�H����4�˜`��1�ퟱ�Ә�^D���������rG�_<�d)��o�Ҍ��ƶ~���L�G�o����.1�oW�[\_��DA�^w��`�B��R���4�*�: 
vߨ�f�v����
TaM�}�:�J�g�����4ݪ`�����������%�V��y=,�aI��
�X5�w�ӉN�Y�p�U�w�R]�Z.��x�g�d�V+8�W�������+��W��y�qA`��/\��{��\����"=DC���{O�i�N�j�>�8����~����}#��{���OP8�y��A��QY�"i��QK�h�؍I;��Yt�?�9�稝<�0����'0~���q<�_0�1�+&pj+G��Yy�����;��Vn�����̹G��udڠT0.�8!��ī��G/�+�.2����Ʃ	ڹL�0�2�� >�\!W�� "/Ҧ�F�+�+�Xګ/ڐ��:֐_m�(�K�W�6�W�5�aq^I&�I�:�x )�L�X�&��ly)�Sn���η��PK   }y�X�wM��  0  A   org/apache/maven/wrapper/cli/CommandLineParser$AfterOptions.class��kOA�߳-ݲ-�� �Ze[/T��JL*��5�ڵ,��d�\�O~�x!���2�ٮe� Yi����s�y��ə����O �1�AADE4��۩�bU���.�K�p����嚩O����*E�2��0���]�y�ꚶ� ���#L��	�+\a��!�N�T� L�T�l�]���C�owϳ�N�EW�H�)�\���!̄���Z�����~4�qL��8��HqY��&��>�:�U-�66)�}����ㄊ�$Nb�0N.�KT*�7]G�Dm��}��`�D�N�~��^�k�E�y!kl�-�eQ+	ǔc�u�L�ٻawL�!]��6��[ D�S�b�^�mkbi�cn���ޖZ�t���L�֬m�U0��&��9#tU��l#(�9����ڼ�攍'�̒��9c2,��S�2��h4����ay��a���DK�$W���	�����.��6r\6�|�pj��ɽ��uS<���m"C�P3ٯHn�P�i~v#�7��-ΰ��9gq�zK��yz�[9K�|l�䂠w��b(���c�|�3��Ց����l��Ra�=@�5���)({���p�
F|�CϚ�f��/�C�W.hqZ	H�Zd�IN�8���G򚖊2�m����
c-N/l���UO�5�s:��r� �_Q��������&����PK   }y�X1�GX6  �  J   org/apache/maven/wrapper/cli/CommandLineParser$BeforeFirstSubCommand.class�VmWE~nXXM1`Q��@$���{��i�P߆e�&���R�G�࿨G=T9j����?C�wv�J�l��fg�ܹw�s������� Ɛ#���Z�l'�{�ܕ��x(�ā#����0�ļ](kg%g����='�َ\�9E7��]�'�M欜;M0�V�YL���)픭J�o6p�f��n�؝$$���P@4b����lk�@8%I���q����T�urg�UZZ�����H䅕M�ө
��ކ�0�h��f�M\"3AӔ�b��$�>8��~�n.�X{�F���u�c���N�j��%\-Gc}��4���E::�C���2���CW��mK�U�̀I�c=����N����#Fh4���#�O�0z0��5H����ʻ���⬎a��g��~AZ��#Sz��c�p��sg	��<�vڞBgoO�wH�(!�2�¸��:�7p�'�}��
�� �����{6sKZa!�4Ih�`EA0���U�XaLaF9�%�����o��/�W��I���>����~a[:��v�%-+�)��ɩqI��׊��W�W7�l\��Z� ]��&�p\A��^����Z�_��ɪ�OY�3b��c�e| L��͗9�(��A�������^�TǶУ�j�U3B	n�_����L&h�)�!�=�ӲeIg>/�Eɬj}E��V������%�%�^	�^�6��-NT��E=1j�|S�๙�=��S2$�u7�"�HD�ܫa	������C��߈�����;�0�m���1��54�u,����X�X��,����bG�#6�̭r���% �М��!"k��?�?�=���<��k�0싯|��#�d�<썱~���2-I��dj�O0v��ߎ0�Q+۵�<�bT��b��	�y
4�.�� M#E3X�YlЂј��I|�[�u�[�^���*��n���h�؋=�'�l�q9�q��R�묫�߇��!�S�i�s�U^8֑��׻큪zw��%�S��g���CqI���Dw�'+�Ϲ��B���s-�W<���ۚz��0Y������PK   }y�X�w5fr  e  T   org/apache/maven/wrapper/cli/CommandLineParser$CaseInsensitiveStringComparator.class�S[OA�f۲�le-E���lA�1%$��I�rIJx�Clg��-�W������g�1��n�P�41��9���?�|��s����v���N�;�s�Xx=~*��^�~_h��J��z\��R�=�C���<
�H��V���PX�k�abS*m1����&M�,�{�O������?�a
�������b2��1��haJ3��WCk��A��`��y�묃9�3؉�a���0m�f���Hv��Y��A����dv�C���a�,�f������c::�!�^���F��~$�n
�P�poPmyt���V��wۼ�twi���F-%�t�Et��;�����eq\R�����ʰ1~.��%;�G���?u~ea����"�\+h_��f�#��L��PJ�z���mxk�Ͱ�MڠU���}O��=�4���v�nIF2S��)�߸aeq�tg�{X$��+I�5E�I*���m-~Df����YS1R�DXS���<k:F-3T���R\���Yԃ��xD8������7T����+�H�˕o�Z�s������'���Z�s\��i��5I+�~ �À��3XA��)���2*�\ų��E�5���M�;�PK   }y�X���  o  K   org/apache/maven/wrapper/cli/CommandLineParser$KnownOptionParserState.class�X[x��ǖ��2	F�MlJ"Z���E���qc;�N�:В��؛Ȼ���N���{��@k��Ih!@�X�q��B(�Jo�ח���/���k{��J�%��_��ٳ�3��s�����^|@;���
e(�mZ�15��G�ب:��qKM�4+O걝��j$ztCۯZ�f��5�qc_��M���wTG(߮��)���)NeG���<�������7�
�]�y��7�)+��A����ݮkh-�A!H�R��Cv��-K�ׂhm)6Hu�h5
��<������'c]����m'�
T2�5��޲�%��ZZ���Lk�@Eϥ�<��G�J\�Hs^Pn^'X�:����yOq1^���Z>(�!�	��_�|EU�$bU7HlD�@��`�r��I�hD���a�٣�]�pzT37��l{��sw�k�� �n�G4'iH��cd�{c$ajv�0����"�Q}-
�(�n���1�7tD�?ڱE�V���߳/�۴FU���'�k�=�
;
G�Ќ���U�hi`7Kt�#d0��Έ�� �( ]FDM9�s����v$e�czBKDa$~����N�R�[�V�F1YHu��&y��+9����(�
�L���JjL����呤}
�I�g����0�+�4�sܲy���g}�n�fw)����"q��MA�L�zqp�#�	��+TA�+�vs����>s)$�N|R��$��a��e�0*9j<��vݦ�V��ۗ�,�K�!�$$4�}%v�4���,SN,�nŖ�+P� �?�9"�sNl���(�
F%��K�J�#��W��!�������yP�K��t��)ws��~g�;G);2�YZ�eLb����Y�cZ�@u�������J|����P⎨v�v��_��"�D�3�C`M}Ca�b�~E�\XWRԳ�c'�#q/��FNإ�,-�2�e�,^�0G�	|C�7%��oQ�<f�K�-��}��!�fl?�qZ����&쎴�LhV��2��h~}@�A��v6ePuo^l�)��v��IV�P����pٙA�X`m�$�I��'*5����)G%�����
�8�Q�\d�ċ�%����Y�*�kz�gg��
��8��nZ��DA4�>�����E}��͏R�_6T'm�����^h���v�	}��K�i�u(I=�3�&TK�w�3�z�_�0�B���^�1ԩ���������]P͉��P�HQ�h�������Z';i���{��vʭ{Y�o]Z����w��5J��r���6n6s������c�ʹ�nՙ�cZ�7Ān�Ь�Iնy���ܵ^�J|��`��?���XԴ},zD+��%�r���HR�ou��e�.U�.�x��<SQ�wnwf	��A�9z{�$M��)(Q1���9TN#<��h�j��S�.Z���њ�)lx����:(�x����J<�Z�֋S� N�Q��s�,e�����w�+1�RWb�Wb�e�"��&���@OQ��O V�͠�!\;���p,�6�F_@M�6Oc�+�l�~���3�U���Q���hk.���'��'��~��4nϠS<�n��jAn�}���%9����H~{}ć�O�sټ.��Ir0Y����X5����s��f?�T�Y��Ϲ����7<��K�o���<(�[;�]9����}E��]�|q/����.�w�A��9��3�����YU{�i�-�#�p8p*�T60�⁙EZ��i��l	�8������������������s������28��2�a�G��I��'��{?}a�|4�3��9~��� �WQ).�N�F̾Hxw�71.��I�6&���Έ?�u�g�CϿ����o�����f��I��уY����Nb�/�O�5(o|{}+��7jo\K ��T���pآ�M�5���i�g���kq�7.GM�W�W:�g�į�,������q�����U�Z\p-��߹��	�NU�Nz^D-��&�F��������=��vo��PK   }y�XmKs�  �  J   org/apache/maven/wrapper/cli/CommandLineParser$MissingOptionArgState.class��[OQ��SJ�[( �xD�Y�"Z��\��	�eSִg����/���A��?��8g���a�4ٝ9��������O �&BGa��,����(ozM�RߵE�n�z�j��V�&�fɔƪ��}e:�)++u״�S���
� D��t���R@b�p<�lv=��Q$T�IB1 �F�\�$Wg�R����Y^��gg�M�ߣ�}������L��zUȊ���,�l�m��⢆K�Lxr�섄%9�^5�3��m�x+1\�p�)K����զ�6������a�^s��ܮ�%��S�,����&�֭��k��ZlTy��d�Eu]ئ��Ű�e:���9��0Ξ����1�;�]Q�O5JHZrْG�i�؏-�Mޛɮv�����"��&�c�0�g�fm�ec�TR��W��R�|U8��D1M��o�?&^O�ەl�(&�v�	�1��|�j���ռ2��s�B�N���\�;�}vC���.t�N�� A.Fym��[�x����χ}h$�?@�K�����Z8�&'�����y����rߠ�B~� �_O�>x��F`��q��b+�v�>��_`\����'�[j�7��f�y�����Q\㛿����}�pZ#��K�O�YPц`����X-�
6�	�,�;�&��S�����.�>y�0V���~� ��PK   }y�XV�{�  �  K   org/apache/maven/wrapper/cli/CommandLineParser$OptionAwareParserState.class�UmOA~��^9��"TŪ i�^)��Ҁ1$�i�߶׵��w���C��/�45���Q�٣`	rD���y晝�����O ��Ћz�5�A�a�v:osc]�-�),}���pt�i�%���V�lZb�;�p��ڞi[[��h*�C�[7�t�!_汨"�^��>hs:żi���ى�a�������Yq$���SNd���w9%V!�k8����ap���or�ɭ�^��j3/T$qA�E�0Đ��������U��ǠTU�pE�U�0|ʡ1��0��g��3AL4\g(ܗ���p�L��*��Y�Ҳ^��$�V����`(���n��*Ƒ�����&d㿧R��Kv�j4.+��p��Z�4�e���*wL)w�a�Wett�*2$���n�I�[��M�-��&h�����4dD��5�Z�����G����0F��2owR�V��K���V�$>��Ĳ�Sjr��)y\���P������%��]��a�Q�sB�H$�C����'�2ERY^H�Od'�������6b���7�{V��(T�F��!G�$�ht�$1B��qo��C��4Gf�(����s��G���r�w�]���Y����"rV!�1r5H���#���5)I�����:��'��Ĕ�XQ�w��q�;4V{�P.C��}�� Y?��a����*����R{L�!�����C PK   }y�X��`�  �  E   org/apache/maven/wrapper/cli/CommandLineParser$OptionComparator.class�TmOA~��zP

UPD����
"�VM�T0�!�oKٔ#�]swE�?�/~#��?�8{WAڪ��fvfw�gf����� ,�E
B*�1Dp�aӲ+����j�X��������UC�[�7��)�p���n�5,��unsײn���n0�R�(�����#~̵*7+����(�Q� ��7�>�3,�4��!Z��;�t��p��V��b���eSrmì\�E	���R��ά.iHⶊ;1�`�a�
jުV�?�P͠ĶR�N~���6���o�R�������>�N@�y��1\�X�33���=�a�j��	��T���nId�=��3�oN�_)=��tAE�a���u@��偝Fm_�o�~�,�E�̫{�6�i��m;hC�>�h�<[�!4���9BD���h�x�}���4��ݒ.P��u@�yM�¦T���\���]2*&w�3��	AK=ւR�A��%�a��+CV(�fAb�LS��*w��0~�{���ɗ�<>T<��%�a��z:]���$�N�ViZi$����Ϥ(���tCa�%=�o��I2� ��?�e�OgO=G�;��_���\�i5|�1��	����e�>�H!��1�D�����ͨR[��J-C��&�E��N�OHǷ	�"�$�e�н蝙�@�}��:�K���`K�u+�Y.���}���
I�I�LW��p	<B����3o�
V=�c��W�ulb�"�c��<�a��<FPK   }y�X7A�ϳ  !  F   org/apache/maven/wrapper/cli/CommandLineParser$OptionParserState.class�SMO�@���$i���~іC�Dm*�T�RH�r�1+��^[��?�kz��C ?��['Bpq�e�=͛��^������(�\���*��Ą�HEp*�X�K��0"M��H��$��>(-��ɤY?L�J�Za%��Iiew	�NwTG��? ��Ĺ�#�C�p|&K���	,8��I<��G�Y$��F�(WϚ�=U��྿�#4���ya�MH��t���t�'��&��|������Ck�{��o��~��N�8_˓i$]��;Ewl��#B�b��]|����	��.�}K��R 4�i-M?Y&�*^6��",޺�*��?S�����͢	xk�zjp_�� p&\���8���?��0���I�h0oLx�&��+@+�E,���s\v��S�g�K3s�ۼ=L�s�O�\��g��(3��^�_3�q�V�PK   }y�XO��4�    A   org/apache/maven/wrapper/cli/CommandLineParser$OptionString.class�T�NA�N/l[���\TT�v�l[�ZP#��A�b�߰ݔ�v��]P�'�/$�&>�E<��"���!��˙���sf��ɏ_ �4��
b*��!�����0�M�.�L[��F�tu�f��N�.�ʊe������Z÷{�w-�J�q�)!��#��^vUo.�S�(�"��[�l�L�ɶol��6RPѧ�_��[ֶvL�'D���%HT��	�|>�a�(��bc��-�)��ｷ�m�PQ�
&#�.<۵j�Ma}���b78B�4�B�w"k3�4eIqK��		�i�̶C�	�(/�S�IH��[^�&����I�-;�d�Vw�[��Vl�LY ���Zrc����V.pʄ�u__�FH��_E��i��W�sA6Eٔ��Y��0Lϛ*.��M�B�w(�ug�5��L�pü�ԗ�m��5�y��`�0ߝ�8�E�B�ۄ��J��!����2�m�>�6�� ��lS��h���f���At.�� �"F1$)�~�A��M��H��`$Ex��9t�s`Q��t����b�'�7�qUN����;��om-�>����h�)�C�@���u�W�~��kA�
�t��Ƹv���itM��3L�&S��b^c�1ɬ��C98�I ך[N���+�$D�p�t�p�MG�ߑ���PG� h�QP�e,a���t�PK   }y�XX����  �  K   org/apache/maven/wrapper/cli/CommandLineParser$OptionStringComparator.class�T�OA�����q����ł-E��SB$��&LjH�m)����k��O��/�hb�տ�g�� M�fvf�7��۟��|��u
"TQ�0<w����\V�	�z��FCxV�f[y�^��A�v�+���m���v*�mp���гa;v��I��t��}�!?�V�;kw�P��04�`���IH5�T�j�T�p�a������o���^�Ò��j��%x6'����8�V��l�x�M�Ӓ.�	�5L0���f`׬˽T���ʬ;����^��Y��n����g�}ۨU�gx�-��'�#Fn���|�_��hR~���co�n�+��n�7��]iK��:-�]�mT����E <b�|XdX�~F}%���)'�q[�Z���w�������-��;�-�CF�q���q����Bwe�m�����5��n��E���`H�Ӡ�N/����"�HF�Ρ}&EA���e��� L�>I&�������ʜAW?BS?A�l�_ޭ��c��	��i�D�ǈ��_a�a��P��)�D�ē�(�(�8��f�q������dX�J+i� �=�ð�-<"M�'g�xbNc�4��7�c<c�`��W7��N�NH:�!��|G�<�3�=�GOb�Ҵ�	/�)�5�n�S�J������[t��C��̇�O�J��a��,LP�,5#�Uy���PK   }y�X����	  2  @   org/apache/maven/wrapper/cli/CommandLineParser$ParserState.class�SMo�@}�8upS��P(P ih\ B�@B��JA9p�$��U���n��ąH��(Ĭ�V���Z�gf�͛7��?��5)�mX9d�Bx�+F�;��/Τr?k1I�v���|_�^�S�X�P�Ĵ"I�ʁ���!]��d�B�D�	w(T�=��n��*c�jUk�jc=�<
S�V�=�'ؾ�XKH�(7g��O����ux�w��(:C^)6�����L<^����d����B}�#9���(�E�zṛ�o���%�j*k���m.�6��ּ��@�>^4�K%P�w�GxY^��Y��&����/�KpZ������9���f�&�>(%uc(�P�6�j���qN4oc����gD(̭��#�/;k�/u����9r����O���I��6����� 6P,�b��l�:g�u���@��M)�)%��&�naĄ�q'&��]^'ܻPW�c~g�mM(��2o4n�_�M�ۚK.N��\���>�ic���D���([*l�l�x>�PK   }y�X�Ć��  g  M   org/apache/maven/wrapper/cli/CommandLineParser$UnknownOptionParserState.class�UISQ�:� !"�"j2,	���R�UR����L��d&5� W��)�<����^�xQ�~3�M�
<��t��M�~�~�y`w���B@EM���M��Hoꉼ���Ķ-
�N�sFb��煙Y6L}U�E�xb>3�m�Q�1,��[s�����p�	�c�OŖH䄙M�9�afg������T9;F�6��Hf�04��9��6V'��X��r��u��6����:k��J*:q�В>�!$eMBu���,!X�"�;��XΩ�E!l��\��u>?�\ᒊ˸Bi,OB��d��8�H�\���uJ.�"��K��R~C����.�������.o�M�HXj4����,�-�;Eq�Ζ��p���m��a;+��� ��=�Y���i�&(����?_ϓ\$�o')�Q9��a�0�83�f��~ߐ<Z�3"�!�K��ۋ9Q,��nW�58����"�����6�Ǫ��n��F�A�o�¯�����u���JnO,�X���y��U�e�Fi�{P��=D��=t��Wy��	���a����3���L� p�w&�;�.}�L�����]��Z����請�z�e\B}e�D�2����_�ߪ��c�e�i�������QO��Q�cmw}�Vb�g�"��v?u�Yt"������Ϫ�"jD���֙A��684| ��+������S�J�H�g��xV� $�r6�1��a�E��$�~wg
�8�i����Na��m	Q~PK   }y�X/���x  �)  4   org/apache/maven/wrapper/cli/CommandLineParser.class�Z{`�Օ��df���B � J�!IHHD@H h�jP�̗d`2g&@,j}�g�֢�>Z����"!�W�V���խ[���v��ڧu��<�w�<2�Bp�����=��s�=�wνɋ�
�l�/Y�6�0�K03n+�:��v����l˷���N;\��׆::��o�?h���;,��ٝa�Ŋ�C��a0�Vn�6[��P�Z�s�J-�6��`�����m	��T���S4��)��/�n�RLGbr���@y�թ%��$����[m_\��1�		MVuE;���d[1�rP�qN01^A��X�����H��d���C����� '�������?Z-��̴^U�U���i� U�5j~2���H��)��y��B5�����NK�p�i�#<8U1�Pt�{P�I�a��r8-!_˨	��n:�eEV�#\hZ������*Nq�"�R�nb6���*ˋR븴�����A�#ɗZU�8Z$a20�g�h.����B�X$�?�y��i���TkC� �f��y�Q��d`wӨݛ���j���N�ޝ)X:�����n����p$�����`9��jg�}�w����nS���hk.V��@��UXM���[Ŧ/ƥy�.>^��s�h�\�aMBJ��h�"��X~�S_6фuGP�M�N�f�'�6�+.4a�YP9L�c�ƨ�uyVw�͏p4Gđ����XU��� ��̃�h5цv��}Q��bId�:U�Ԯ��>�#�~T ����O@��^���߰��ԋL����+JA55х͂�HWs$�Eu�Cݦvj��n\̜��ܞ��?\+b�S��Jl�%.5q�&X�E
F�MQ]���E��3UC7�x�^_8�D9�*W��t�?賷�je���uj�Z���T:�ev�6�`�F|�BCA*��a�b�3������6�VU���}�T+B2�K��$>7��bk���f���h>�NM�|Y8b)܎)�&�0�SeZnK(����
���.��`K�t�>UD��7�b�R���1q/���&m
2��2:�Ҫ���&��%�t�J��M�Ha��6;�Җ�z�ďU�qK��΀��f���Cx�
�`[�]�c�ʛGM<��07l�{/5�c�W�ZNbw�}�����0�V7�Я`䉁�s��I��=�V���6+��g��+g�����ƳJ��n%���������ܐ��XD׳�/�x	/���D��W|N�@���Dp�{��&ۗ��^Qa����o�	����/ˎ˓�K��xփ��ůT��d{>:�L�r��K�K��Ѓ��E��=�n�}�k#�;HF$����X�JU;�&>�HC~��#8�X�J�_&�[%�J��v�%�ז��G
���_�W*ҩ��y����!%�#Wsm�����k�&>��	�5O������ʾYm�b�����fR��uzJ�,�d�25���SD�8n�Fu�nɦۦE�մ���vq���4�)9B�t���pn�ώ��'��-&7m�BI �Q���
u��2�),T**}Ƙ2V���5�i7d��P��V��0���ܐ	D�L!A����Y��qXɮR���j�݆�"��ҧRv�H ��-v,xd�`R,%
�O�L/�G
� Oy��B�����$��4��S����B)"4I�z�5j����F�Q�Cd�?��^k�`0-d��RԵ�n01��Ѭ�[�Z8�l�L�;K=r��2�tSf��3j`�� k0��gG�aۗ���vwq"����k�Mc��}A��lx`WU]�H��lu�ᆮ�f;|n�L��2�b�Xa���:��~�U1܃K��s:q6�p|l���Z7�O&z���d�E��s��0$j����K4�K�x���)A�nS���JT��?! FtyCqM�-%al�33+N�`�u39ۣ�ě�_�3eا�Xwg"�2�3Ļ��ֲ��g]w̼�i�.����a�	��u}ְOڧ�鼈�F�PJ;������h��Fү�T����0!Si���|��aIe��W5�I�8`�U��E^z=�[��ueL�&J^:�@%Tg�T[i�3�����n�Ba� {�@V�I�Lh���f�*ǁ�s����6$��ك�KG�U�����=In��&P����ͺ���w�J�Zu��=��%��E�[N5yL]��ՇL1GJ�"���bG"S�TTN��_1{���r�)95×3��}@�|%07�q�V���?��r�>v��iu�[��~?ㆈ����A;����/Q��`2�#�L��1S��X�2\��>�R�̧/ƨ�kC�%h�j�VĮF�`��o��ʞt
C�s׆�j�_p���)C�3���#�2.�!w������#1a�k�Kd7仂�㹃���LÆ�s����-V�N�R�	C�?�{�Ui��L��)<�O��sFBd��d.�Gã�՟���Wn�ƫk=~�V��g�k!�F��#����ń^���6��� 8�lu2�of�\*��O%�0.�|�R�Kzq
ŝ�ԋ����kqӌ^����Oj`aV�D�k��b2����,�:�c�,$oL�,M�S2cJ{0롤X�Vom�������ETv1�i�Ԑv6� [	�~�~ʥG?�6�c^S�c��C���{Q[ߏ�M%�ҋ�f�a�}��_쐗��Z�3X_�,�:{p��q~�z�w ��&ź�a���u�{йH�u�$҃-�.��_%�6\�	fz�y{p�\W����(Z�kx��P�^O�3�t�f:E�:�qR��:]I�I���( ���މ�~l�F��4YHT�Rf{]}�Nv`��劯���/�+Mi�)//��tg�w{p�"���W�-�n��?؁|�;a.U��j���ܜ����<Z�/e�5��t���HOR��W�Ȟ�?ۃE��HU7>�ӎ��"��-{�aϡ���-U����ԃ2�63�[0>L����l��\�Â�����܈lG��c��Oq^C�A�!����YF`����r��K1�1
/�F\*�q�l��҉+�\)��*�W��ދ��	� ��Fy7ˇ�E����1n����pGVv���H����R��vL�e��o���j̠ffl����X�1YΔ��񸹜yӈf=��9�b� g�Π1^7-�HVp̅y\V�ͩ2*��|��zf�x��4�*��y_V˗���$�ES52?�����sl0p��1��p����oQ�_�P���ޏ,�8%k?\|��8:���5�6Xs��B�ı� 8 "1̻��ߓ�yF�r2�0���M�e!_�1���-⎼֏}
&'�<I z��x��^���,�D}x>k��
��JGY^#1�c0��՛���+]11o*1�B13��Jy�B~�0bRr�9q)k�[�<*�t܍�^w�l��;���[�N������>���P��Tb�#x(�;a���7���ß�}E�\��Կ)��v.IZ���[\;���2��}�x�s��w���,�$�A�>��z�c$��$&7esF��1hQUiܠ1�Kc
�=2b����r�149�Bc���+����r1N��OX����`�j"���D���m؇���S���I�����Qd?��!�\<x����1�f����/I^�-xEnƫ�^�����M{7�x߆�Ҥ��+�P�c�y��/0o?�T����u�|Eӭ�����۳�g�Lo�%�4����jy��ܢ��y����J��r�l�b������|B4q�L��̈iҞ�������c1�2$@
���è��@�!�x�@�ğe��0�~W�Bb�~�H�8��?�g���A��A�	��c����Sm��~�A^���!ޒ|�ovY8�_
�z�zG������peI��O�0-�+T:Oډ��=r��R�9Y�eW�9��g�@�����>��5ME�&���R��
V��x��[,��h����+��b��XE�J/�Hº:["�K���D�ҥ�X,������~�Vy|<1ڐ9 C;�N��)F�Qu+k�
��,��)O�Q_�Tu�\G��{q^i�cV�S��x�0�_
����x�9���^��N�#%I���W9X�����M�����R�k����L�%Q¡Wu�&��=]߳�og�^�������+��#���h䎮�����|���)ng��gI���r]휜�3D������.�X���Й���9�Cp�U�;+t$~�A�0e�M�s�6V�X�)��#�w���V��\�&�e�̹����̗g\�8����+30�1��ܿ*y*��g���Y=R�
��G�����!��Ky2'���_�k4��r~]�|�N	�sc�op���ɒ1����C�i;Kn���|~�σ���]9r�o��6���y;�lce'���}'�w��n>�ud���܍	�PK   }y�X��sP  �  4   org/apache/maven/wrapper/cli/ParsedCommandLine.class�Wiw�~��-��-�`'۲l��M�LL�b"��v�2X�<D)����I�n�o$�7����(�t�֞��~�hz8�Ͻ3�%{�Ѽ��{��y����>��_ ��w1���E�@�ec�H;�>{�9�
4�l����hh�TJ�*�O�I���pZ�kh�юmCE'�6J����^4�L;}�1J%�I����S6s'������Z�)��Xr��]>�<�:���Nv���j���}�tc���б]�%�,��{�3�h�hh�����q�1��V��vy���(�)���N��H�4�QgJ�m�^$4���8����pMǸX`j͖ݢ#���_ga�ߧ��8��WG��VT��<�xT)��:R��j{G�v]�k���QW2�y�z'cF8�!��x?���͵:t6$/+ъ�%zG�^hm`�G�V<�1�t| �GH�R����6��q' �f���0�8XN%�/�b$�I��A��J�5��ܙ�e��;������hL�8���^��΢����mT���q�Z&v��>^�
9Ӊ!�p3��rqN�4^�cYK;'0�x��A���ҋ:�p��v�Վmd`-�V���#�(�z�
�	��)}\����e�h��%ɶ3��mr�pl�k�9Q,�Z�y�Z�og�pw�{^��e,�|$��� �AMYx��-�7
U���:o*��xE`�D��`�7a�Dμ��sC1�(���n�L+�pE�U�����Š�\N��	�ī���x� �ݗ��9�i+on�!�s�#��� �V�1����Y`[jX7�7Q�ܜ$s�D1'??��Te���M�x�8ofǒk��#0-K��8J*�*q��Z���~0�H�]���yr�K�Ԋ��#&~��X.U�8��aC�j�v����!��DP��`Y�����'��rU\����t�Q�*���(��(�mG�o݀yIq���Um+�Ys�,�>�]��)�|�I���M'��;��>o�������77����FY�1
=��=[�_��Z�d{Xl�����t��̛�,IɮzC22��G#��tqy���k\��)�y!��?�5o����h�3��P�, FhA��k�;�{����b;(�oa;�]�o�g�.�L����u<G!)�7M��0ug"����]<%��vG}����b&Ӕ�b�Mݑ�8����VV�6#��s�b^@/W���~���y��c��i$aa Wx��A�J�ϫ�Q��xM�:�/�K�p��|�2?|_��5|�x|��Χ��MJߢ| �U����V{5��yK�0��p�D����<���$�ѷ�������p��n��g��"R��q��:+smW�F�`��\��~�!y��*�w�l�~�!K��&#c^92	�zDJ>����D,?{�M�?��ѺUxle��ۘ�������sG�{�`�2�g(�a����E��I����?"�!�Q�C�s��B���������2�5��u��?@\CV���Ҕy�>�oR��$�	���L�ƅY�5��X7j�ƔB��M�H����:Ɏ7hN�-r�C�->#|���|�{�T�L%I�K���XY�w�F�.��,)�"�#�.��/)�	��À�CY��Z��~�@G�����ƣk��)��b����,OɾbЗCrx2�w�{.^�(2H�݁���M55d�q��%�0Oy��^����S;Q�R�5C�G/ۂo~�������0��kT�Zî�x�7�������ӳ~mui�9UB�}{]YR�\WV���_�p�_�7�E�/���$kRj`]����������U�'�Y��٣�P�3!jw(���]Q�~�?PK   }y�X�v�E�  a  :   org/apache/maven/wrapper/cli/ParsedCommandLineOption.class�TKOQ=��-��Uax�m��(�G������IM�.��N������čLT�$��e���@���E�wN���=������y<������ ��[|��7+���V��tS�s�D�(!����l�P�,������ݐ%�ȸ�^���UQy����Z�ۚ���x��Yj���W�jj��z���r^7�����M��FKk��|[Hhd#��'#�~�P�7�"�1�!a�a��~�0�
7
6����%���2�:J��5�n+U��psGqt��h��`"ߦ,ؖnV��b7FpM¨�1(�κd4�]��jÞ"9Vj-����5ӂkJ�uLSBE���j2�9���ę�fDvJ�nR6/�;{�������Izs�ְw��br�e�٩�^/�/<��Җ�e���z٪mj�k�i��K�(rK���:�1���E �5ě�@"y�C���ox��������a�xA����%�Ҫi&%�O�����-��=Ӆ��*J�D�������&���|���c���6!9��DS`�#H�E�}���g��<�d7w�q���ǵ�qeR��C��c�\�����!�iV<x��b҇_��O�~G1N�@[/AA&0�I$hy��O�����	}�,9>TzQA��RH�&O�=�5�=W�SBD� �9D҇7�M��&�����S����{�bn7f� >"��tR��q��ةR��Eɢcd�/PK   }y�Xx��͊  5  H   org/apache/maven/wrapper/cli/ProjectPropertiesCommandLineConverter.class���N1��
"��n\�&0w^Ÿ"J�q_��P23����te���-�18Q�8�����M_ߞ_ b'���H0�CM*��1�:�|@�{�x�r�@�g��V��%���j2yԮ��j2�C�XDB�2�J��r��C��+�%OWL�s(0�7I;�f[w��I��9���N�S"�N��~թ4¡�����T�f�h���xc|�o}�k�MKV��-R7��X�n�v=Ӻ#z��}İ�Ӈixk!#��R����<�ݦV"�u����4�v���7��oʾ��R������ {X0?Ŏ4��+&.��k23y��	��Lf�lbf$na���؀"VG��'pe��ه)t���D7йit/�L@�����kX��;PK   }y�Xkn�4�  &  G   org/apache/maven/wrapper/cli/SystemPropertiesCommandLineConverter.class��AO�0��nK��Bi�1��2���\&q &�V;�2&e��M��$��(�j��v�؇�p�2M%�I������=�m����� |�Q+(հ�2C_*��1���|N�s�x�r�@8�D+��JIJ�2y4���2��C�LDBb(�;#X�2l&i�)�Ʀ��ÉK�^�v������S�/GWv�z~���4�=�9f��U��p�Yw��%����C=����1�o|�9�F\�,_�%=�y��O>eh�����u����ڝ�w>�N�#�q��oJ��HsЄ�8g/�Xu��r�<�,2��������-�5�Q�>��u�9fff��pvg�X^���0��,@�|�+�����ʏ5����}���YG[��A�����A��FVu� PK   }y�X��c  H
  =   META-INF/maven/org.apache.maven.wrapper/maven-wrapper/pom.xml�VMs�6��WluJfL�9t\�%��jl�c�qs����X 4��HJ��8�N}����ݷowIE�e/�4��jt�� E*3.������u�1��_|߻�)
�	f�0�XJ��4S��3ލ���@GT zRA)	�Ja_ԆEX�KF 	��=��'�o`��2�['J�p�" ��H�K�ò�ۤ� .�P:
�)[%�6��+�T�W�in+Hn{���R}Yw��v���V/8N��Qw5z��s-��4Pk��\�XbI�ʪ�L��um��o]�0���U r9�3��2��æi�hR�a_UxG2N���zޣ(PkP�w���� ��H�D�`X=m?\�)w�H[��x���;�zVT�@1��	L�|'���&��g�sx?<����M��<�^O�ٔN�0�~�L��'��%�u�,w"ȭv����n���CW��%O�$��,G�%͸��P�*�����-���ܸ��ޫz��i�+%�cj�vE�Q'y�^PE�sv~N�9��Z�-�6��p���ӳ����K�s�����R;�L���M�>Y�5��y��D�̰�8vwQ�g��U�nab7eQ�d]M����)�|��B�=��3e����-��Q�p h=��P|\�Q�-����y?�E
Vb|o1��b�F8g��u�xe�~����d�	�E70�T��vW���@b;��	�4wc�w?A��v�A�x-�
iE�Qw�lM��pД+S�sЈ}�v�C�8?��]jzal�o��e�����>+n^M�>Y��;��*~���F���37�MJ�O�e�����3�U�Q����[G%�[��̳��K_a�L?�㵨y�=Y��i~w��mh:�mLk,��o��T�\q����1�:�iѲV鶐m�aE�Q��y���V�g^�� �V�=�1'�̙(6csӓ}����?�=��t�_��rM�ݔ~�l� �tGn^�Ya�@���~���N��-,
C��Y���c��PK   }y�X�\�@   H   D   META-INF/maven/org.apache.maven.wrapper/maven-wrapper/pom.propertiesK,*�LKL.�L��M,K��-/J,(H-�J/�/- ����%$&g������g�����q PK
    }y�X            	          �A    META-INF/PK   }y�X�1Oe�   J             ��'   META-INF/MANIFEST.MFPK
    }y�X                      �A�   org/PK
    }y�X                      �A!  org/apache/PK
    }y�X                      �AJ  org/apache/maven/PK
    }y�X                      �Ay  org/apache/maven/wrapper/PK
    }y�X                      �A�  org/apache/maven/wrapper/cli/PK
    }y�X                      �A�  META-INF/maven/PK
    }y�X            (          �A  META-INF/maven/org.apache.maven.wrapper/PK
    }y�X            6          �A^  META-INF/maven/org.apache.maven.wrapper/maven-wrapper/PK   }y�X�G�q                ���  META-INF/DEPENDENCIESPK   }y�X���m  ^,             ��V  META-INF/LICENSEPK   }y�X��w��   �              ���  META-INF/NOTICEPK   }y�X�۱A�  U  3           ���  org/apache/maven/wrapper/BootstrapMainStarter.classPK   }y�X܇�H  C  2           ���  org/apache/maven/wrapper/DefaultDownloader$1.classPK   }y�X�4'0  �  S           ��s  org/apache/maven/wrapper/DefaultDownloader$SystemPropertiesProxyAuthenticator.classPK   }y�X�3�    0           ��   org/apache/maven/wrapper/DefaultDownloader.classPK   }y�X�y]�   �   )           �� .  org/apache/maven/wrapper/Downloader.classPK   }y�XK>8ڤ  {
  4           ��/  org/apache/maven/wrapper/HashAlgorithmVerifier.classPK   }y�XXW1�  *  *           ��5  org/apache/maven/wrapper/Installer$1.classPK   }y�X[/A�  �#  (           ��r8  org/apache/maven/wrapper/Installer.classPK   }y�X;n4GR  %  %           ���I  org/apache/maven/wrapper/Logger.classPK   }y�Xb`3�N  ,  /           ��L  org/apache/maven/wrapper/MavenWrapperMain.classPK   }y�X���|�    >           ���X  org/apache/maven/wrapper/PathAssembler$LocalDistribution.classPK   }y�X\�@j#  �  ,           ���Z  org/apache/maven/wrapper/PathAssembler.classPK   }y�XR(��  c  6           ��#a  org/apache/maven/wrapper/SystemPropertiesHandler.classPK   }y�X��   a  '           ���g  org/apache/maven/wrapper/Verifier.classPK   }y�X�W!  �
  3           ���h  org/apache/maven/wrapper/WrapperConfiguration.classPK   }y�X��e��    .           ��5m  org/apache/maven/wrapper/WrapperExecutor.classPK   }y�X��   T  ?           ��Rz  org/apache/maven/wrapper/cli/AbstractCommandLineConverter.classPK   }y�Xm�v��  -  I           ���}  org/apache/maven/wrapper/cli/AbstractPropertiesCommandLineConverter.classPK   }y�X�:�dP  g  ?           ���  org/apache/maven/wrapper/cli/CommandLineArgumentException.classPK   }y�Xlk�I  �  7           ����  org/apache/maven/wrapper/cli/CommandLineConverter.classPK   }y�X��I�U  �  4           ��=�  org/apache/maven/wrapper/cli/CommandLineOption.classPK   }y�X�#�ر     6           ���  org/apache/maven/wrapper/cli/CommandLineParser$1.classPK   }y�X�@Ƀ  �  I           ���  org/apache/maven/wrapper/cli/CommandLineParser$AfterFirstSubCommand.classPK   }y�X�wM��  0  A           ��Ӑ  org/apache/maven/wrapper/cli/CommandLineParser$AfterOptions.classPK   }y�X1�GX6  �  J           ���  org/apache/maven/wrapper/cli/CommandLineParser$BeforeFirstSubCommand.classPK   }y�X�w5fr  e  T           ����  org/apache/maven/wrapper/cli/CommandLineParser$CaseInsensitiveStringComparator.classPK   }y�X���  o  K           ��s�  org/apache/maven/wrapper/cli/CommandLineParser$KnownOptionParserState.classPK   }y�XmKs�  �  J           ��̣  org/apache/maven/wrapper/cli/CommandLineParser$MissingOptionArgState.classPK   }y�XV�{�  �  K           ���  org/apache/maven/wrapper/cli/CommandLineParser$OptionAwareParserState.classPK   }y�X��`�  �  E           ��=�  org/apache/maven/wrapper/cli/CommandLineParser$OptionComparator.classPK   }y�X7A�ϳ  !  F           ����  org/apache/maven/wrapper/cli/CommandLineParser$OptionParserState.classPK   }y�XO��4�    A           ����  org/apache/maven/wrapper/cli/CommandLineParser$OptionString.classPK   }y�XX����  �  K           ����  org/apache/maven/wrapper/cli/CommandLineParser$OptionStringComparator.classPK   }y�X����	  2  @           ���  org/apache/maven/wrapper/cli/CommandLineParser$ParserState.classPK   }y�X�Ć��  g  M           ��O�  org/apache/maven/wrapper/cli/CommandLineParser$UnknownOptionParserState.classPK   }y�X/���x  �)  4           ����  org/apache/maven/wrapper/cli/CommandLineParser.classPK   }y�X��sP  �  4           ��{�  org/apache/maven/wrapper/cli/ParsedCommandLine.classPK   }y�X�v�E�  a  :           ���  org/apache/maven/wrapper/cli/ParsedCommandLineOption.classPK   }y�Xx��͊  5  H           ��c�  org/apache/maven/wrapper/cli/ProjectPropertiesCommandLineConverter.classPK   }y�Xkn�4�  &  G           ��S�  org/apache/maven/wrapper/cli/SystemPropertiesCommandLineConverter.classPK   }y�X��c  H
  =           ��F�  META-INF/maven/org.apache.maven.wrapper/maven-wrapper/pom.xmlPK   }y�X�\�@   H   D           ����  META-INF/maven/org.apache.maven.wrapper/maven-wrapper/pom.propertiesPK    7 7 �  O�    
```

## gateway-service\.mvn\wrapper\maven-wrapper.properties

```bash
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip
wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar

```

## gateway-service\src\main\java\tg\ngstars\gateway\GatewayServiceApplication.java

```java
package tg.ngstars.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}

```

## gateway-service\src\main\java\tg\ngstars\gateway\config\KeycloakJwtAuthenticationConverter.java

```java
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

    private Collection<GrantedAuthority> extractRoles(Jwt jwt) {
        Object raw = jwt.getClaim("realm_access");
        if (!(raw instanceof Map<?, ?> realmAccess)) return List.of();
        Object rolesObj = realmAccess.get("roles");
        if (!(rolesObj instanceof Collection<?> roles)) return List.of();
        return roles.stream()
            .filter(r -> r instanceof String)
            .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
            .collect(Collectors.toList());
    }
}

```

## gateway-service\src\main\java\tg\ngstars\gateway\config\RateLimitConfig.java

```java
package tg.ngstars.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    @Bean
    @Primary
    public KeyResolver userKeyResolver() {
        return exchange -> exchange.getPrincipal()
            .map(principal -> principal.getName())
            .switchIfEmpty(Mono.just(
                exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "unknown"
            ));
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

```

## gateway-service\src\main\java\tg\ngstars\gateway\config\SecurityConfig.java

```java
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

```

## gateway-service\src\main\resources\application-dev.yml

```yaml
logging:
  level:
    tg.ngstars: DEBUG
    org.springframework.cloud.gateway: DEBUG

# ponytail: swagger-ui only in dev, removed from SecurityConfig prod path
springdoc:
  swagger-ui:
    enabled: true
  api-docs:
    enabled: true

```

## gateway-service\src\main\resources\application-prod.yml

```yaml
springdoc:
  swagger-ui:
    enabled: false
  api-docs:
    enabled: false
logging:
  level:
    tg.ngstars: WARN

```

## gateway-service\src\main\resources\application.yml

```yaml
server:
  port: 8080
  shutdown: graceful
  forward-headers-strategy: FRAMEWORK

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
            allowed-headers: [Authorization, Content-Type, X-Requested-With, Accept]
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
      password: ${REDIS_PASSWORD:}
      ssl:
        enabled: ${REDIS_SSL:false}

springdoc:
  swagger-ui:
    enabled: false
    path: /swagger-ui.html
  api-docs:
    enabled: false
    urls:
      - name: auth-service
        url: /v3/api-docs
      - name: client-service
        url: /v3/api-docs
      - name: intervention-service
        url: /v3/api-docs
      - name: media-service
        url: /v3/api-docs
      - name: notification-service
        url: /v3/api-docs
      - name: report-service
        url: /v3/api-docs

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
    env:
      keys-to-sanitize: password,secret,token,credential

```

## gateway-service\src\test\java\tg\ngstars\gateway\config\KeycloakJwtAuthenticationConverterTest.java

```java
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

```

---

########### intervention-service ###########

## intervention-service\.gitignore

```text
target/
*.class
*.jar
*.war
*.log
*.iml
.idea/
*.swp
*.swo
*~
application-*.yml
!application.yml
!application-dev.yml
!application-prod.yml
.DS_Store

```

## intervention-service\mvnw.cmd

```text
<# : batch portion
@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.4
@REM
@REM Optional ENV vars
@REM   MVNW_REPOURL - repo url base for downloading maven distribution
@REM   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
@REM   MVNW_VERBOSE - true: enable verbose log; others: silence the output
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
@SET PSModulePath=
@FOR /F "usebackq tokens=1* delims==" %%A IN (`powershell -noprofile "& {$scriptDir='%~dp0'; $script='%__MVNW_ARG0_NAME__%'; icm -ScriptBlock ([Scriptblock]::Create((Get-Content -Raw '%~f0'))) -NoNewScope}"`) DO @(
  IF "%%A"=="MVN_CMD" (set __MVNW_CMD__=%%B) ELSE IF "%%B"=="" (echo %%A) ELSE (echo %%A=%%B)
)
@SET PSModulePath=%__MVNW_PSMODULEP_SAVE%
@SET __MVNW_PSMODULEP_SAVE=
@SET __MVNW_ARG0_NAME__=
@SET MVNW_USERNAME=
@SET MVNW_PASSWORD=
@IF NOT "%__MVNW_CMD__%"=="" ("%__MVNW_CMD__%" %*)
@echo Cannot start maven from wrapper >&2 && exit /b 1
@GOTO :EOF
: end batch / begin powershell #>

$ErrorActionPreference = "Stop"
if ($env:MVNW_VERBOSE -eq "true") {
  $VerbosePreference = "Continue"
}

# calculate distributionUrl, requires .mvn/wrapper/maven-wrapper.properties
$distributionUrl = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionUrl
if (!$distributionUrl) {
  Write-Error "cannot read distributionUrl property in $scriptDir/.mvn/wrapper/maven-wrapper.properties"
}

switch -wildcard -casesensitive ( $($distributionUrl -replace '^.*/','') ) {
  "maven-mvnd-*" {
    $USE_MVND = $true
    $distributionUrl = $distributionUrl -replace '-bin\.[^.]*$',"-windows-amd64.zip"
    $MVN_CMD = "mvnd.cmd"
    break
  }
  default {
    $USE_MVND = $false
    $MVN_CMD = $script -replace '^mvnw','mvn'
    break
  }
}

# apply MVNW_REPOURL and calculate MAVEN_HOME
# maven home pattern: ~/.m2/wrapper/dists/{apache-maven-<version>,maven-mvnd-<version>-<platform>}/<hash>
if ($env:MVNW_REPOURL) {
  $MVNW_REPO_PATTERN = if ($USE_MVND -eq $False) { "/org/apache/maven/" } else { "/maven/mvnd/" }
  $distributionUrl = "$env:MVNW_REPOURL$MVNW_REPO_PATTERN$($distributionUrl -replace "^.*$MVNW_REPO_PATTERN",'')"
}
$distributionUrlName = $distributionUrl -replace '^.*/',''
$distributionUrlNameMain = $distributionUrlName -replace '\.[^.]*$','' -replace '-bin$',''

$MAVEN_M2_PATH = "$HOME/.m2"
if ($env:MAVEN_USER_HOME) {
  $MAVEN_M2_PATH = "$env:MAVEN_USER_HOME"
}

if (-not (Test-Path -Path $MAVEN_M2_PATH)) {
    New-Item -Path $MAVEN_M2_PATH -ItemType Directory | Out-Null
}

$MAVEN_WRAPPER_DISTS = $null
if ((Get-Item $MAVEN_M2_PATH).Target[0] -eq $null) {
  $MAVEN_WRAPPER_DISTS = "$MAVEN_M2_PATH/wrapper/dists"
} else {
  $MAVEN_WRAPPER_DISTS = (Get-Item $MAVEN_M2_PATH).Target[0] + "/wrapper/dists"
}

$MAVEN_HOME_PARENT = "$MAVEN_WRAPPER_DISTS/$distributionUrlNameMain"
$MAVEN_HOME_NAME = ([System.Security.Cryptography.SHA256]::Create().ComputeHash([byte[]][char[]]$distributionUrl) | ForEach-Object {$_.ToString("x2")}) -join ''
$MAVEN_HOME = "$MAVEN_HOME_PARENT/$MAVEN_HOME_NAME"

if (Test-Path -Path "$MAVEN_HOME" -PathType Container) {
  Write-Verbose "found existing MAVEN_HOME at $MAVEN_HOME"
  Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"
  exit $?
}

if (! $distributionUrlNameMain -or ($distributionUrlName -eq $distributionUrlNameMain)) {
  Write-Error "distributionUrl is not valid, must end with *-bin.zip, but found $distributionUrl"
}

# prepare tmp dir
$TMP_DOWNLOAD_DIR_HOLDER = New-TemporaryFile
$TMP_DOWNLOAD_DIR = New-Item -Itemtype Directory -Path "$TMP_DOWNLOAD_DIR_HOLDER.dir"
$TMP_DOWNLOAD_DIR_HOLDER.Delete() | Out-Null
trap {
  if ($TMP_DOWNLOAD_DIR.Exists) {
    try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
    catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
  }
}

New-Item -Itemtype Directory -Path "$MAVEN_HOME_PARENT" -Force | Out-Null

# Download and Install Apache Maven
Write-Verbose "Couldn't find MAVEN_HOME, downloading and installing it ..."
Write-Verbose "Downloading from: $distributionUrl"
Write-Verbose "Downloading to: $TMP_DOWNLOAD_DIR/$distributionUrlName"

$webclient = New-Object System.Net.WebClient
if ($env:MVNW_USERNAME -and $env:MVNW_PASSWORD) {
  $webclient.Credentials = New-Object System.Net.NetworkCredential($env:MVNW_USERNAME, $env:MVNW_PASSWORD)
}
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
$webclient.DownloadFile($distributionUrl, "$TMP_DOWNLOAD_DIR/$distributionUrlName") | Out-Null

# If specified, validate the SHA-256 sum of the Maven distribution zip file
$distributionSha256Sum = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionSha256Sum
if ($distributionSha256Sum) {
  if ($USE_MVND) {
    Write-Error "Checksum validation is not supported for maven-mvnd. `nPlease disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties."
  }
  Import-Module $PSHOME\Modules\Microsoft.PowerShell.Utility -Function Get-FileHash
  if ((Get-FileHash "$TMP_DOWNLOAD_DIR/$distributionUrlName" -Algorithm SHA256).Hash.ToLower() -ne $distributionSha256Sum) {
    Write-Error "Error: Failed to validate Maven distribution SHA-256, your Maven distribution might be compromised. If you updated your Maven version, you need to update the specified distributionSha256Sum property."
  }
}

# unzip and move
Expand-Archive "$TMP_DOWNLOAD_DIR/$distributionUrlName" -DestinationPath "$TMP_DOWNLOAD_DIR" | Out-Null

# Find the actual extracted directory name (handles snapshots where filename != directory name)
$actualDistributionDir = ""

# First try the expected directory name (for regular distributions)
$expectedPath = Join-Path "$TMP_DOWNLOAD_DIR" "$distributionUrlNameMain"
$expectedMvnPath = Join-Path "$expectedPath" "bin/$MVN_CMD"
if ((Test-Path -Path $expectedPath -PathType Container) -and (Test-Path -Path $expectedMvnPath -PathType Leaf)) {
  $actualDistributionDir = $distributionUrlNameMain
}

# If not found, search for any directory with the Maven executable (for snapshots)
if (!$actualDistributionDir) {
  Get-ChildItem -Path "$TMP_DOWNLOAD_DIR" -Directory | ForEach-Object {
    $testPath = Join-Path $_.FullName "bin/$MVN_CMD"
    if (Test-Path -Path $testPath -PathType Leaf) {
      $actualDistributionDir = $_.Name
    }
  }
}

if (!$actualDistributionDir) {
  Write-Error "Could not find Maven distribution directory in extracted archive"
}

Write-Verbose "Found extracted Maven distribution directory: $actualDistributionDir"
Rename-Item -Path "$TMP_DOWNLOAD_DIR/$actualDistributionDir" -NewName $MAVEN_HOME_NAME | Out-Null
try {
  Move-Item -Path "$TMP_DOWNLOAD_DIR/$MAVEN_HOME_NAME" -Destination $MAVEN_HOME_PARENT | Out-Null
} catch {
  if (! (Test-Path -Path "$MAVEN_HOME" -PathType Container)) {
    Write-Error "fail to move MAVEN_HOME"
  }
} finally {
  try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
  catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
}

Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"

```

## intervention-service\pom.xml

```xml
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
            <groupId>tg.ngstars</groupId>
            <artifactId>ng-fields-shared-lib</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
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

```

## intervention-service\.mvn\wrapper\maven-wrapper.jar

```text
PK
    }y�X            	   META-INF/PK   }y�X�1Oe�   J     META-INF/MANIFEST.MF���
�0����]�N� A����Fss�}aA]���|�0��>�=�^Y�!f�<����7����"��VGe eˉ��%-�VIL���V5"�_�VA����s�~ν�)K?7#��P�2'�1*ۆ��k;��H���M�����n�|��ӓ�PK
    }y�X               org/PK
    }y�X               org/apache/PK
    }y�X               org/apache/maven/PK
    }y�X               org/apache/maven/wrapper/PK
    }y�X               org/apache/maven/wrapper/cli/PK
    }y�X               META-INF/maven/PK
    }y�X            (   META-INF/maven/org.apache.maven.wrapper/PK
    }y�X            6   META-INF/maven/org.apache.maven.wrapper/maven-wrapper/PK   }y�X�G�q        META-INF/DEPENDENCIES��A
1E�=E.����N�u���4	i�����v��!?g:�Lʙn�ڤ�*ph�΂F�PJ#[1�!;����V��~^y����ŃU���_߁�����ʦ��k
vGЅ#| PK   }y�X���m  ^,     META-INF/LICENSE�Z[s��~���r�Si��4i牱䆭CiD�n&��%� ���. ���=����dw�VM�5I�ٳ�����E/˝�u�:�^<��?�u�t����B�Mv�������<�h7��W����\�6sc���ʽz���~Z���J��Y]-�7��xws'>��qw}{ws��-~]�SW������	�j.�T�;=�rn��k3�'�	��M#Z%;1�Ie['dW��t���bt�V��Tc�_^>[i7X��{!��pKU��A�U�B��֌۝�N�>hxΔc���X/cO+M�z���w�
P	�� �8�������s+��l��v[z��!S@me#�I�c�$핐%I	Z��Y/��^A�o�i
!�
R����cW��Ҵ��$����a�rxùxg,�я�71ɪ���G3/eFGq�B_�R�W� �Y�*�;�w!#J	N����,`E+;�U�<�׍��+V��N������$ٹe��	�\hЄ��v�GI������%������/i;�a�A���:� �d�A�Fu`�R�+'�3=��6�L\�Z���]�^���&�Q�y|x����[�<�'��$�ְ[	)��GZoU�����kM��[���p4IY�������3�ht�qw�3����r�!8���#A^?P����v��;��Q|�l��p�����cl(?jkZ�����	Q�9|R���o��R�yH\1=��qtLH�^cBR�s� g��'��N����P�n�*-�p��c4��	(��KҘp#-����1b����ZY�<H��M�?å���>�dą�n`x8�[
�dV9X[�BA[/��e��ΰ�Ⓥ�W��#$Sc���
W����h7;� ����$�AP|#:��T�p�~��*܊܅����r��8k� �iՃ&Wb�i|�66|��y6yaX唃H!�K��4��Lou�����NՓ�/ı���0���H��V�R��T��)h:F��j��'2��㤓��N� D��%�"��Ѩ'J�u�����"�����9S6�/�'\��Q6�	�p�H�d�6�
~J�"K�Q���M�m7n ;<x�A�E��z>h#��Z�L���j�De��}���5��i��e�^��f^��˰H5��� 腍l(���uD>��[_`�FW�Ph���d!����R�+��K:"�7@)AZV�"r7���5wTXBJ���	v?V>f+�k�F/2�DAfm�p�rtT�iǖ���ȏ�x�4��`��YC<�Q\��ь����B�M�(P.��#�PD�a�F"��l��"����4���u<v���R�܀���Ѧb�l�PFEHJ���$t���mK��r��7K?����H�p۷���Y��������f&K��TI�H ����� 9�S��� �	���T{�\�3�K��Ǘ�z�'s��pxY[�4�S"��Ts��ᆡۂ�c=��	�%8���+B����@�߀�\j}�E޷�4?b1��ϔs�v�3�J���\�2��`�r�"����K��Y3�]a;�������6u�<��j ~�Q��1<Q���`&�M�>
�ʾo��48�����U+����lv8�"	ɭq���uNZM�Y[@���(j_����`�)_���DVOˎ�q��-��$o���b���n.�5�?�B�
c::e�[VAn%�L ���T�"��ƹ�d0<FiF�O�</E#�n��Q[.`��|�G���QM`ŝo���29����S1LŦ�(ShF}��F#�/y�Uqu�E�X�.�
���ҰO�
���;�O��u+	َQpPn3��gX�i#l6�Q!���7�"O�f.�O Y�Z!2H
�V)�rm艸��z�셼䓎i[���~ܪ�Z9���!��TR}8�$��2��d{��&Qi죰硎���Aw'�=�l{���([�-C����e��U$Xxs��Sw .�8n���Kձ��] ,V
yS��	
�!��?� ��s�����g�A�U�-T<&��3��p�INK��h�%�V�o��ճ��������q {c��=�rg��ٕA��L9�,�+ZO	>���)��Y�"(I��fb<�2�A�ŗ�5s��g�J�2%�S���/I�
�6}ԔA�d�d�IT�gu�>�I��y=@	]'����M�T��ũ�e�zٔ��g�Te
� �Y �V/���s�0#�P���wa�_�f��M�[�8�"5��P����"�:Lf�lȪ�[�w�̤ս��$
��G�g�~
�U��jlm�DL ���;�1��`���D�*虘��8��0O�[�5Q�*��Ұ�	���+s
���UƑ�F�:a�g|흹2b1�]���hS����Y<<ъ�ӹ�J$�ΦyI��۪I��g�D�1�&c�ةu�|K͎�	�^5�@7:�����a�Rc�K��8�8��l�����]%��;r��m���Ӛy�Ejf�"��V���ׯ̀���՗���vK��R͍P��_ad.�1��)X1�D[��(�>C�#S��� ��7Ī��|�t�{���?�3]B΁)wv#���jL_�5�lqnN��}����:��C��C��6ժ�F�o���;�	�tr)~���4jV�Q��+bӁ�ړ�lȦ�7_Δ �ԟ��J;j��Ҷ��]1	���7��yc��`��H�K���a>�]R�uš�q��?��ˉs/q��?[��r=?,��u0�����7������bu��^����Z��X�~_����h�~��K'ф+U6&MDsRp� M.��"{
�`��������z�\��[��z����?]߽��\��|����B���~u���^�������N�~���Y_s����o@�6�t�@73�N�<gMo5�s:pх�P�%���<mt87��v��Δ:���������E�i3˱��9|&�E���.ϗXyПn =X|�аt�N;���,�!tj�h`_��,�mw1����g�������!BG�mq�-����v�|~0zN�e��M�� �V�r;�����J@z9��
�ֳ�gH( �|���g�x!��ƙ��j�w�X�c��[��F��9F���ygf��O.��Z���5���&�~��l�^�9����R7��j$�z���"x�M������+��q��x�e�a��4]����� o��r����\,J�	h�����"�,)>O��������B˝1<�I�䲝f���jExPGʮT|��Ǡ�w���Ւ4c�6Awa6��Boy���̗�Z�<�/���Ac���c'ĭd4�3��Go�tMv9���!���4�(�KL'ݢ$DO��,�L{&]3>c�s��m�h�J�Ю�
`�ՙѹ�-!Q �ъ)�Gk�m��&CW��*Q�ӹ����F:�-�l��>�ƌ6F]8��WWXWϽG�/no��?ߠiZ �z�/���o��>�%���.(�k�iB����B>��F�:�Z��r
$;��o)D��_g�h2��!�����Nz..�L����@��A��.u�Ԧ:�	@��;��vv7������"��zV p6/��i?'(N�r�@�!c嶋hf�q�Zݨ��
ݐM.��r4�F�a���|��_PM<���½kϤ!���o�9�e�/��U�Bz��G�����>H��g��O��*.������("�#\���<�x��6��1FT�8"u�fC�29م@�C�Ͻr���j}�T�%_�П���3���N�p�K������H��&�������h��ێp@	�,t�o��iI����\��PK   }y�X��w��   �      META-INF/NOTICE}̱
�0��=Oq���:���Ap��|4g(��rm�����_>����(B
WTwayi��A�����0D����ɝ�VQ	z^r@K��sCLD9,�A��*P~6�J3@s�g��frj�Z��/�S���PK   }y�X�۱A�  U  3   org/apache/maven/wrapper/BootstrapMainStarter.class�Wk{�~ǖ���	�pJ��M�eǖJ 4�!B��vj�R��t%��M�]e����Ҧ)�׶h�Wz/��/��~���~)}g�U��:<�#��9��9������G �G1t�SAHE�m�-S�̹�d�^t"#�i8�t��O�E����n�C�=�ѪZ��g���,�Z��ۙ�,˩9��k��w4��m��f����&�c)����iX�Y��gNhNy�?��`����ɜ�ʍV�Z-gi%�V�M@m_�F�+�Q�;�5
��m�픷�+;6ÝH(إb7��+k�M�6��(v�U�T�|V`���l3U�gNw�j�>�#'<u�%\��S�+�vQ��f�������S3~S6�$C8��
U!��ɲ�k%�-ź-�{��]�T�y�c���Ψe:���shWjS+�xP�~�O�h��V���}�CQ�-Zv�TKW����rF����/� ��P��U��}��c���0���
��DM��y]�Ȉ������X�5�Rw�Fn�Lf��yT�a<&u��^�1jZ��xB�O��F����	�b�8�xqk���(�Q};rE˜��9�]0���>� m�P1�����] �9v~ף�ͳ��d1��W�i��g�`��K*�q����S��W�l<�!h����l�M)��@�O�i_Vq_�s3F61�\�.�ك�{6Z�o�&�#)��/��)(y�� ��ɪcXf��SPVa�<�����m�7l��l�}_`��m��p��(*��Q2Cm�H��li���B����[{e]c�=����:E�������o�͂1xh���W�1*��&�
�pI�WU|_������[�j�5�tVJ|Cųx��1]����~Sŷ�1.V�����^|��f-j���|G��x��P+���j��R�e�S� 	v4�� ��2��sĪ��'����.|?P𒊗��\�rVQ�0=CS��,�xn���5��Q�^)%M�IVHv�S֓㲡%�^�<vx*i�Mj��)l�r�d_o�/��#?V����R��Ef-{^#��S�u��O�	����a�6ϋ�����Y���L�'���>)��x��Ok�!�Mb�),%��\B���|�w��fx��i�w�rw8�Ãi��$wY��M�p*&|u���7�晶}C+t/��ڮ�;#��٥�[�z�#�Hͭ,e=ો�JK�_�%�{��
<��i$Pi��[��Tn�^�0�U]����"��d�_��U����Z((o�R�׺(/��B^���	g��cx`�:_:�S>#.���6�.~�Q����(,�ɧ�vy`p��l���mӝCk�.��;�{{Vp_6���Dh�D(��2�g��k��k8$���3����5�w�OO��_���tg�T~g��DD��1ݹ��3�]�����Ѱ���x���^^���}&�/3��\��s��:z��L�Q��K�v���_��t:����:��)��E�Z��w�=��m��H�?5ak���k�"�:���	�EF%�{����`�MW�_�Ӄ���� ���Dh��p�|{F��g�C�D9.3���9�kL��=��;���'B��2�&B�����{�W=W��
���V��A^z���s�!�4m���=Ȓk{0BT&�F9���+�;�7Z�<{x���Zvs�����-��������E",��ɱ!�Ғ\���!�	��(�*��?~B-��7:r
�|���ׯ@�%C+���n���/PK   }y�X܇�H  C  2   org/apache/maven/wrapper/DefaultDownloader$1.class�TmOA~
�ak�"��R����O5��Zb[�m��.��.�^!|�'��h��Gg�HP �Knvfn�yfgg����?<�3CY��.��͎p�|OHw_��]�ݲ���}9�F,�:�0ư|.� vx?j_���N�1����^%\����������+E���aG��o�Pi��ܗ~��0���-$f�C����W�u�+9�$�d�^i�[+nV�墍똲�r0��(C�eۭ�B�%����cXȔN|�/۹�i�n�%��k���͂�۸cᮃY��Ka&B�u��k�b��g#�����gPoy�:���Lk"橖`��|)*���o�v@�dI5yP��7��3f��a�,��,jU��]꟦3�ȳυa>s������5��M��7��N�����l�G=�`�Fq���;xb�n�����k��Ά�B{�)z��+b��b���$��;�4�H[��x�ť/^<���(f��(�xH25�BY �L6���
�� �%,�ˤ}E�O�@�]��7\Kΐ��[����!Ia�GLL�H��El� |�6�Gb.�1��1�,L,���PK   }y�X�4'0  �  S   org/apache/maven/wrapper/DefaultDownloader$SystemPropertiesProxyAuthenticator.class�SkkA=7M�q�>\5��Z�Z�X��VQ"BI�TՂ��4��d7�N��W���?J��Y��pv��9���wf~����2��H`�B2�a��UwE[��m�]�{J��R����4�j��7�#���A�ek]��=r�����k�&t�#/<��/	C�����ѺD�|����v��-�����E�j�������ŶQ|Jec�.���
a2�h
���LF�R�V��)�V�_/�ϧR�FpN��J!K��2�7��^H��-ܶp'�i̜uع*�V��<��oUl֞5;6G��Qު�7Z	v$a����M��-�;���Sj��)�g�q2�^Hx[��'�L�2�ﻓ��\ܒ�o73�r��*���'������F�Q5��3ɜ��WB���KUi�_ZxD(\\�0��YxBX�<f�"�`�!��2�0�3�G>�.���A��;%�b��� ���1�<
q�g�Z�;�N��q窓���!n���c:�ˀC�=���p|B��QO�1�s�/��+�ľL���QY1»X�����/�r��oPK   }y�X�3�    0   org/apache/maven/wrapper/DefaultDownloader.class�Y	`י��-k�bB@lAJ����C.;68��W-cc �A�yF� w��&i�6=��^��3i�*��p�݋�v�{�g��}d�nw�f�73�%[N	�y�~���+ v�c
�Z-{&���Ԭ���Ofⴭ�r����Io0�g�hPԠ@XsB?�'��9�9~�H9�{3fƹ_���u�!�R�i���V�ט�Yg�u��Zzڰn�J6�ҝ�e�s�@xp�]ұ3�L�d�F�ZI=\q`°�|��ѰN�zS�2�33���3�g�0�ܸAC���T�X�GET`�����9I�@ްC؄��E�f�\e��|�1�V�RIg^`G�rZ�O)�E`��[k����1�KU�(i[�۶	�l��;{Z'B؁[�hhE��Wb�pU<By��)l�]��Ut�041<y�@�l�w�_�2�!!��󔊝�]�������^�t\��!t������ќb*�[���"c%�3Y#1�1O��<�t���W�} �#���O��g2y�/]%s�6���l�������Z���UЧa����4J��6�K������A����C�/8�����Ti���X���1�flF�eg�6coJ���ԔNŠ@���|�Q0�����906�i���d.X�R#lB�&q�:����,�X_�^Ux�@�{c�˘y#�<cF0��N�9V,�#$FH��K�Ů����f=��g��X�%�o||4�QpT�1�M���8h���<8����K�cB��RHSݒ\�cئ���+"�&�e~Id�f@458ց1�cMI��A��N*�j��Y�n_��c�)[I`��k=R朆GA�6��t��Ϥ�S-KRmɗ�L�h(H��Sz6U��29�ΐ���LKr��C���|����;*3Y]�b&#Z�|E����j�E�;YG�.�2ٴ���AF���a-K��Y���S�ƻ	V	����e|}��
����>I��K�XvJB� >���~gY��}�v6AD3d�I��29�-Z|L7�K�⯱З����\f?��#���jg�AK*���!|�PpNÏ�G��Ey�=k�UF،i�K�e����S���3��F����H����K_|N����`y+{��ee�U�D�s��/QF�r��0tڤ�d	�X`J��e�e����R#~
?�����y�X���C��vzq?m1�?:ػ��X�������C[��X���_��U�@��8/�PKMy�L֯ ,�׍��"I�YҢS5k��R��K��5\�\��Lm���7|����,i�֏gyW�s~����i)Y����� l��B���|�qM���%�>mdٵL��������d����_���[���i��[��j"��)�L���o����d�p�iLr�m�#[)�8kѝ��͔�6d��{~@��{�z��F���;I���?R����v|ɺ�u�u��أ˩xK���r�!���7�
�R�_��5ϲ�˚������]��2�S<Oi[������5C�F��A��2j��mc:���(��?�Q\��6�����w0���.Pw�/���*�/��o�2Ati�<e�d��S�
~���O5���_���;���-eT�fu�un15xc��������n\����v[�i,5��R߼#;��-+i=ܧ�����?�2��VE�������e��3f:�Wwtg>g�2�b	�M� �є�g쾌���h���T�
�%�
���Z{ٍ-����g�m�b^U7%�L>fZN,��`Q�t�s�V������*n�-C�{��	�	�1���b-o�-{^�(��&���%�R�P�N��Bi�j��3N��`Ul��7~jnq�&V;+7�u�b3���b�[���l��"�*b�&�����-�n'2c##���y�R�*ZxܞOl��Z��{��[+^	"Ѧ�v��`pڲ�t��,�����bSԣ���֕/}_{���`�pa�a�K/IŤ���c2��f���P2g3�R'��OE-5�7��,8�����M�%нZ��VY���h'	5c������-V��.�5���&�4��q�H��\O�قk
c.�����w�,%KU4�j�\��,��R�۸r�%��\ZګxC��ː�_�c����lF�Z?X#�6�U�1XBD�
��5^���t?N�9.��l����PH
nd�qZ^Z��T�Zy�cW�����e �X��&e���.���i�Z�v�	y��-�����CX~��\�>AS�\Mp��z>��.����4�u��9
����T'n��F�� ��Hb'Iu���؀]b'Wb�%n�����d\���A�َp�\[n^��kX}	�)q[�]�wy�vi��Sw#�n������]r/D�ě��g|����輆�������˒Y���U�l�{-w?{ ͼFl�@�m�L���H�>��YNIi�:�/���	��x\�ѿ��&��1TD������w\��(�f:�*��5���c؁$�1A�OVع�l�.q����K�-�4R� �͢�}B�>U�T�JO���Jcw����Ga�(��h |*�P�O�Kxl�"~����xW�=E<���C��ß�O?�">[��`4Pĳ�JT����S���\8vTN'��5�*E��6w�r��jݍQ5�X��Fի��~q�9��y������Q<���U�6��|N�K�@�.�
t�l�1�8D���G��G�ܣ��atb�8�}F������@Ӥ=C�iҜŗ(�|���8�zb��;��E����ە�O��}r��	y�(���w����M���%��uF�q_�������� �`X�n'�+w�/A(��6�����&�ۨ��g���=��Q�~�|��|���_�/w�,!�5���ߠ[�:/�<�K�e�P21�z-�]ôyD��h-��U�S�:��*���*̸���w�q���	'�iT�rζ�E��PG�ϊ�F�oN��w����!�^���[��݁N�u"�U��hS���?E|�;^���wD�|#"�Շ�Q�@w�P�""�(�Uʩ��4��O�I>=�t1l�wr�q��	l��]�OѭOs�{���$����8�~�bG�R�����83� �<��ô�I4�sO�1(���dt�M&��ek�u-�
�gĈ�B;�^��.d�o �S�!P��;K�x��1r�#�Νn2rS�p�5�̄����5�Eqc�����E>�+�7?J�|���qt�\Vv���)��b%�v1��E���U�E�+��K�:yr~�GT>O�}�-_D�g���&>�#"�Gw "6�gCD�E>�q�|*�-q��u=�7&��ɩ�hCr*&��̒SjGrjM��?J4�\����q���S��Ӑ��"�,}�9֏���_`�|��2(��m�Ÿ�+��W��	��m¨��[�T���n�h�n1ŷ )�-�M���aW�#���ױD��Q�'q��?PK   }y�X�y]�   �   )   org/apache/maven/wrapper/Downloader.classE��j�P����

>���lܹm���~LF�����u��C�%��a�p������Z��u'�B�D�,5\9)
u�l+�[���F�\�s1'~?d��~\#��l��-y�y[�*�|Ls嵔�r�'�/_�ej�G�¸�zw<���.�WJ6A�nt�@h�V�su��:��g�w���PK   }y�XK>8ڤ  {
  4   org/apache/maven/wrapper/HashAlgorithmVerifier.class�V]wE~���4]�J�V`)��Z%A��@J��B���d�lIv�f��
~r��x���xQzD�o��x<G~�zA}gӔ����vfv�y����y���x��h��ï � C�>�Ǌ����LN���<j��s�����B#GXA�^�6��ذ�T�8e�E�$���I�G73��`$�Lyڱ3���^� ���a��0+�3�"6R�H�1,3�fl�hQ�[����Pa�h���Yv2(��В���SHL�ֳRs�NV�I��m��h�_�a�ӔeD&�I��K
v`'Qꖳ�C�p:J&%�۠)�-S�����SS�6Z��}�(��D��}�ϰA�f�n�lm�,m_I�D������&�٥��#щA�^���3�Ŭ���ALVF�D��!�UW��a*�W8��׈�gg�Ȧ��+FrDd0�Z}L��A�=��u[UuU��eβK:yy�N�L��/�Dt-Jz���$uz�RJ��_�k���<��4���e:�tN_w�"�V�JM'���]A���^i�$9�*8�T->�1����ыď����(CK���D��czѐ5�պ*ZA�4"D�K���Nh��JT�xA��ù�Zu���4F9�V0�K�,;��z� b%}Z��[rj��|^��a�,��z�q�3�?;�蚎Q�od�w�B�`�nH�˗�!N/��K[Z֨��IWB��m�r�zg%#_p$A�T���A��OK椀V�uٕ��®���P3E�x�9�U�"c�jH!����q��{q��=��u�9C�;Q�[�Jc�l�a��251z��R�lJ�8�&�=*ﳬ��1�6��Ҧ�)T�����M�.Ǵ\�2�}�����D�H���H&�,
3�X�z���l����[�W_F�~���z.2�j�Y�S+�ah�k��mq��J��v�k�{ٕ�O*�8t�<u������g��e/-S�Ok�f/����U���	��T�1�E�^7�}R���Uj��Sg�35t'�+F3�9p��]�8Cc��܉,�J ��yE�q�
�=�] ��}�a����~
ŹT�<Z�B_�Y����G����j�1���y��N�|��K�J<�r�p{U�H�#�Gϸ|(�C�D$�p��7n�6~��j�5|x�I�-��0�kmZ���������a6�������?�͖᠇�M������O�\�î��<.!Wːyz�<�K�7��S��7�<�>��A�}��V{�F���	%�\�&��5�;���4K	�������m老)pJ�f\C��n�Y�`��;tn�L?�������u�^������+�V>o5M+?I�F~�P>u|�d#H�na��r|H�,�O�/��6���=�ѹ��r�*�-�x����!�����&G����"��,���O��3��>�PK   }y�XXW1�  *  *   org/apache/maven/wrapper/Installer$1.class�UISA��$$�Q�QԈ��w��X��@��lghB�0��� ��'V��Ej�KyPO�Q��;!@����^��}�_�y��ǧ� �b,�D√���!��e�W�� �%�,\��+�[y7��?3��.� s���rH�a�h��px�/s˕�5/a�ʥ�#nҲ(z>��HW����l�@
q�M�@'�΍��/ �9�P0��+l4���B.[Lb���mb�2toF��wDPuB�������Խ	
��[�\�:7��ك&�q��/������ar�Pk2�'VlQ	���MbP\=&��(CrY)�C�7C_������@��slU4�f8ѷ�"����E�Q����.����T����N��R��¨*2��m�g�)��m.xs3��KT>�F�$�Ӯ��@J�b3tm�p��ِ�n�J=���^�Ín��{L����(�����p�lM��T�� ���]|rV�]V}:�B��zd�K%c֫���l�q�'�9�ʄk;^ �r�v��p�yE����E��n�20�+�q���C�L�z K�U�U��6#y�V�4��l������ڵ�8��`4>A/��kV8�>@�bZ�U�d�8MjuXN�ͩ��h��������$݉(�Oi|F�='�����ՠI�:�1D�'53`�"J�S�RG�O։��NY;��O�|^��J4�8]�J��3Z��ct�sd�������}ž��8�Ʊ>c�^�fl-Z��$���z	���
��@��v��Kz���6�[����	=?PK   }y�X[/A�  �#  (   org/apache/maven/wrapper/Installer.class�Z`Tՙ��d�;3��Bȃ"�Y�	IPz��$�����`�[�V��[��Jڊ�<d�nkպ�v������vw����]�[�߹��$���*s�=�?���������z�E ��y����Q�B��}��&d��k���gvڂµ�p�^'�/_��^E���hw�1:{̚^������i
�l#2�w���,# 8�9熆$��S�L�13���v�Ź7�'����:J0[0-b�=���ٻב�<�����u^�a���:|8CP�sێ�s��
vǣ��ݦ������xbff��fǰaӮپ��9�,�0���X}���5��DK�T$~����0�د��X��s���c�:oMk�W0;u���V���y�2�kX���9|<�@�{#��U�ְBY�F�tj�����V�|�~Y�lu�Le��J��p���8Op���H�w#C��,��U��)g��O��c����["���VC����PP2�N1�	<a�n����*�vȮ�\�-ߩ��s��l}<b�x��n��Of������e���AG#.fv+��s���eb�$�r�QG��z�lF��	�6�hq(&�ɠ����1+�M5��ڪ��l�,	�T�uh�Ѧ��m[�E/ڱC��::�S� gd4[��*l]�p�%(�f�v7v�T�*�*�ԱG�WD(������`EĈڊ�SG ���Q��0[�{CN���6��xPaխ�Ar	�!�6��R!4'k-�Y��i�U0��^<�%8������5D&$�+2��Q��i�u�q@�K�j�����5nm��и���ֶ��KXU�?j�c�~8H��Zh⎬��J�e���O���J�B�1��u�Z��z�\�3z�civ$&�!�s̮+
�o�GǮ�qC0��5�*X��Ѝ�	�뫹���:���(�SH�'�Sf4Nefy�R�n��^"h��t+����jM�y*~>��~��S<�������V��u��tq��+Z��ѶAe�J�@0�Y+:�W>zXǗ�q7�9�RΤa��u`{�`0Q��x�'�m��tT���u�v\*�n*g�)8cT�m��5�;�d�<�oi����p,U�A�*�۵m˖6�y��FpE�_vvl�?`�1?k���
�F0�7��&Z�o�ph��]�p�o��vh��2�J����èˊ��Ŧ�L�]Sr&�70�:Н*����s��.��� �)�ӕGN�����ܘɚjv�m��}���b�@L1�����%0<h:�U�/v�X�P1Z���f�+hƦh0�� �<�Ѩ1��ۋW����/u����C�f_Cj�al�ΛEғ�1�~��5��`ޤ���7t��B��`,��������Oh&#WS�۩�yK�?��BAgȊ�~�R�!m�Z}�^��?��'���8Y��#I�s3��w0Y�k��
 <���ꋹ񯄆��pM��o�����Z����羅��G��^����?u��T�5�a'�N�Z�`�*�qC�Z���xq�R�x�մ��{���!�0���`�H���z�o�Ĝoڒ�7�OP����N��l�d*�������Z��{�sW���L#f�vO0�=Άq���Xq��U��I�d�
�P}��C�M���(O>M�[�a�=^��$_���V�l�7�$m�Z�-��w���=)�.n��y�[	D�եHx�,��f��w	�1�/���s�t�ܗ)�m�.����+���33E��cD[ͫ�f��T���^��/�KVz�D�i2G�l�Qz���O�3�L6�}Fh�
���I��Rmy{0T�6k1od�.�U�ʛ�<DT���\5�[M�*�6,�e�,!��V�v��͒�����Y�t]&�U>v�Zqu�.RIӛa[��7�Ah�{��u�H�e�&5��� �4+o�l6[Q�1d��kLm[�˹�](R�BrA*[6A�F
2���	]�W7��x�r�.u����7Q�d�hra���a��C��lg��pg(`�5���Y$�i�~��ň:t�����]w��0���l��΅Wʨ��70M�\*�h�N���ᤊ�`۔n�9�����ͺ��6���)�۩�U���������
5�2]�I+1��]�������+��&홨9ʏZ�-�t(0H$�[v����^��+5��6D>9�"���i� خ��6VpܲW�Na�54^\���mOo?ۚ�ook�Ҳgk}�F���6b��I�W�)|-����4�k�`��h�͖x�^3ڦ*��IܶѠzON�TU!TN��g�؍��}��GY��{#�/:���O�����c�D�3����`쥙Ƹ�y���Xc���f�&~����?��t�a���YՇi���>8wؙ�o��ϱ.��ܼt��l�XG�x��ܿو85��BeڍB��4���c���
e�r4�P��,�������9����Æ��ĵS���dU���J������KG1o��S8��h��Fw��^�jtۺ���<i��HTh�T�K�)3Qˉ�h�3�;NY�4+4S9����υ��DI՘ �����ԃ��3���N0��۷��Vb���]�����\2)��v����m���N3q��48�P�i��p�`2�f��	I�����p�����b�W$�sf:����ZLL�t�-�].>gWA**��VQ��UT?�Y�8�'��-���b�P�0k��B��lEb�e�?��s����:;�g���Nο��L��A�n�ϙ���`������4�Q���T<�Y���-��Zm��.u��������Xt�:�pQq� 6(�KFp�`[�m��׸�ݱ/�cwT�	}Zrާ��
�C�'I����C���F�J�U�<#ؗ�ǧ�^�Ց�4�jFߎ�9W�q;��z*|�!\S�-uA��~��;�a�^Zped8ͧ�\|���>[ Eq���W4�C�t�}A�l��S
|Q���շ§ᡣ�_M}Ei)��Gիg�����)Q��u��xO?M-�:�}*���OT���#x>O	4�Վ�`z<<����d�xJE�aa/b���7��F��%��F�¥m�v4���.lA[1��p��Z1�6|�o��st�m���{��Ӎn\)%�+e|������ގ.ٍnك������'G�_EH^@T^�-?A\~��N���x=��e~y�a��9�U���#K"N��A��(��+���،����s��B9)8*@��H���X%�� �i���oK�\�N�oe�|J>Mn���_��k9sg�`�)���z���o�Vj�4����������9����P�>���Xnt0C�&Jz�G��Mi�[!W����6W&��0�*��ZK���q��q�L��qY�+H���0�*_�~��GO�Y�2�q���b~�����W��\��A���:Q�ϟ���I��>>qR�� �Q���)ב�z�~��FF���sw]�����j�xJ�Z���W�g�V�����߱1���y����m�O�]�5e�s�������CZ��=�5k8y��X�)oJ��=q��GnL��2��~*I�@��@�χ�:VY�/#��<l.~g�n����8�%-������t>�1Q�x�Ϙ�y��ݤі2��;ZwR���z��^��O�CL��P����B�C�����]��֧�^�v6+����A��y/�H�i�ݧ�o�&��5��S�_b~R�f�95��԰�i)Ose�L�O�+�ס9�Ct�,K�I���FsH� y�{{���bTQM�<.%CR:,sw��af��3xNO�9]>��u�|_Opϻ�sE\y�jD����#�P0$g���r�
#R��q5Z��յ�լ	��q���V�i�#rA5\|\�v$}\"��L�$�;$�P���Qx��c�s�����xX6�`q����s;�������/c��=(m*�<�uv�U'�{|^�t�GO�+�b>���Hg�-P��g��Ǩ�!��	�3�q>������x?M'iSx���)�e�އ�6#���	������	\���v�D��'���%��_ë��~�8���>��r�<�>���F �����Tȃ��2�!_�/9��Nڝ��CNԪ��*j���e�������[����Oa��\Y�I��ئ�j�zO����$�:M�X�x��$x'!J��i��	��%#������4�
m�H�*�ic������:�Kdg��~F����б�Jr�����cN��ǹ*y������PK   }y�X;n4GR  %  %   org/apache/maven/wrapper/Logger.class�S�NA��-l�]n�� r�Ee���ŀ!�Ú6�3�aYlw���Q|0Qb��C�nT(�Ϝ9�������/ �WATALCzw�>7jܵ���]Q�z������l)�8
TIhS�����a���p��!ӳm!�������6�ǀ�AC���>7}Q'
o�Sf�q<�t\����\#��Zqu����漫�
�
F5�a�a��#R��m�|����)j�*��vsٮ܁�	��쨐2�)��J��Fq���%G�4C�ҁ��r
�q�e�
�b�C{D*��kX��m_��]dg/�Td��আ[��0tV��y5�]�jp��n��&C��Q0��+�{�-!��Etӫ�Z�K'������4���,B�Jwۻ��Dc�7��B����y�S�7NH����j������ɪXu�t�E� �.-l�V�a<XY���N�N�yYF�g�;�!]"B�Fq�N�U@P��&p��M�uRw�F�Ч�~"�.�h}C��EC�~���O"���Q<�#���Z�&�'�/������C���a3�f;`�6lOO{�3T��c���P�ٜ�<F�;�PK   }y�Xb`3�N  ,  /   org/apache/maven/wrapper/MavenWrapperMain.class�Y	xT��o�&�	F�d�	�"	H��	d3��5>&/��̼a�M�V[�.jkKWk���E�I0E즭ݭ����k�ͶT���&��d&�������{�9�Y���=�"�ω�*�S����q�6��Gk����@~} 0w�W�;�@���B�Ft�V�h�1�6������Q-ѣ�r4`:�@X���ګEJ�+ڭ���v$�k�5s�N�_�b	��&����f@�	x�3��*ʥ�D�R��a6���LE)�,N��ף��A�ΛQ�:�ό£u
������Nq+���j�e��֩݉c���k�%)<,P]>��'v��"֩X/5v��=��FlR�YE9*�fuB�1:���y��A-3mM�V�JA����A��h��CE����3����`��e�-*�b���-�m�,-Q#������ۡE(p;.Vp����.ʴ+m������<m��:��%P��+D�f�W^�=
T4�I�<������qӠ�W�сt�P���, >W��W`]V��p�ԂA=��r�KV�f}D��f�p8hhD�m�Ih<|؉��]`s�d�N�����j�����c!\`$ ��Cf�+6e](����A݊^��^�x�,���m5B���*�r�/�_����f�=x]v�M`���e�����C1��}�5屮D��5*�p-��n9�h	d����$�� O&,�4d]�j�#�����p{ �7�q�>�&}�/)+em�L\8,��aLE 
�Fx$0�Rֶ�Q����!,�>4�����]��q��|���,V
��8�"+�	x3�%[�S��ɣ�*�����9������I�$vg�8�x��x)�%�&�th!��
^�����t~���T��q��<{K/�7�����\��D� k塂�|��������@�U*^-�F��cZT��z��;%��T��;��.��xk�U5��^O�#9qoT�&o�[�VK�jT�5�������B瓤.oSq�����}�A���P�N�»�̮m
j��m1�ʤ�	�f#du`�f�v�����hm:��^�O��U| X�0��4úψG�D��y[��R���aQ�Q|�e2��vï�VgV�lu������>�O*8��>�O��N22M���m޲6F§V��|Gæv�{į[YA����Yk���f �'�xS����MP��zǢ�a�5��ǁiVR�H'N�a�U<"��T5�l4��|�gU|�'��Cq-Kk��\qe�c
���Kx|~إ�d��������mO��s�+���p�0��:�u�)�헐�0v�l[܉`t�Kw��Ys��ZLwo��\#U�����T��!��W>��xޟ�<󹤉��&�p�I|[�wT<����Vʥe/9����c�����6T�u�$��Q���j�|8���Q��(b��$�{�?ď�P�atJ�6�x
]c�\�=;R��0;V����1[�Y�6�Q-��l�ρ�
t�(����G�xx��|O�;W>~��W�5$��Y|��+��,���}m��S�;���D�>ʦ��9����4�	��XD�ˮk��_$o�Q�W��y��ŪØ�_�Q��w��MbG��[�<	&'��?�Kſ�}�$����A#�+����$���ee�i�H�Y� K�"rRev�D�1��xp�=7�g:a_:�A�� ��u:Km���D�m�8{��1���sAU�t4�{;��|ޞ�֮/g�bU,���'.��xb�*\��w�foKC_{�P�2^�2�O
�۟�ut�~�LKb�nR}�X�R�J twuu�tu{{zۼ�d�LIg��e�6��`\V�4�g$����<}���oϨ��C�ޞ�.��!�mQ$kgCI�fH�>�@WO�Cl�Y����b�s�M�VY6i��u�C�h��<�L��Z4 �	b�9 �+�������[�U�K36���׬}I�i6�v.����J3���SN�Z{���,�L�GL;��Zrƾiط�����s�qߥ�������A�|�ѰfZW����kI��4��)�L�V��N��ΟCL�d�8���	T�i!գ��yqY�r2�ޕ����v�xt�m���e����q��\)V���|�r�4�L9�.kf����ڔq!����K��xC�x#ǛRƛᄐ���RNJ-G��X䙄��b��3�"���� *E��ZԐ��E
�����^9�L����)��3o+'�ҵj���1���a
����y�p�.���Ϊ)�ޙ_��F��4Z]�ey��;�
�F��z&�7��i\I�U�zVՋQ�g=����b�A	�AO׌h�l+ц!t��;�n�h����V\�w�
�B�W�-�ʡ4%�ʥ�kQJK�I�b��F+6b��Hlg��Z/�%��Vl��{r.� ���Qq�4��騬��(�]7PY噀1��#8|"y�R
|�I/�чe��JXJ�m�	�X+.;�t1��:QO	�,9��J=�b;z���7�����I�,S��n �3�p_�"���j��*Z�l�P
06Z�7+�
�fu	T-���+f�'�v E�R��RВ��&iTJz�rK�k]wO�'�֜yGR�%J�%��D�y]�	�}���	||�q�9VNb��!�����=X%����/�B�`��7��N⛧i�o���Y@��D���"DÅ�!�yb�:DE�&�	�=8�����L�ᨄ� )y5*l�h"��=
8#�[��z
.�r�Lb��G�k\����>�c(��3���L�g������y{%��J�o&�ێ*��𧁪NϣPH�G��v~���(���B��}��%"�3)��"{��F�T���WA��;7%"�'���˩�X���73*o��J9�q�\y=�ކ�n��]��͌/�Q��Y�\��^�B��4��	��#�J<�6(خ�����#M>Μ�r8�5^t���Jfl�+ma�b�D8��S�Y"
�D��,�_G�'��M��$��ú,���Dr'7��㻓Tx�^������^�労�hMf�K-L�%b9�NlR"ʬ�|@ˀ�~����+���
���PK   }y�X���|�    >   org/apache/maven/wrapper/PathAssembler$LocalDistribution.class�R]kA=w��vۤ��֪� %I����R������mv3M�lv���-��?�?�xg�m�A|�g��ޙ�����+�xpPrQ�QA��8"�E2
އ�2�	�#����Pj�5,������:գ@LE4��D\�$���t*u�A��Y&'a,��~����\�p��4!�CN{J��V4Qip�bi��Fh�Gݨ��/jJ�o�a���M��P�D��&�ԟ�Ch�yB+�/�r>V�M�w:��#����6Z�		W�Z'��{ә��<iޒzi��]�Hݍ㙋�����voX<�v�8h�[�iޔ}	ğ��v���惰�w~�:��p;�X���Z��`����#�s����iO62��x��	؛�J��o7ͫ�m�9��hH�,$��%�
���}�m��-�	���PK   }y�X\�@j#  �  ,   org/apache/maven/wrapper/PathAssembler.class�VYW�F�$�X�$��iK��p�f�l�%8eR��T�+ؒ#�@��������+=�š9m��W�zzzGR��vO�iF�{���e�̟���;�����
xx%T���ᒼ GR�6�_Rf,�����Z�<���j���P��E7�"rF�I*����h�EC�d#2*[�n�T��b0��'L���
ÖAې��Y5���]>ԡ^@��M�3��?�=�6��e�Ru��~N�zU�2�x�Yi��v+21%"�H؊ C���MeX�v�K��v$b����wIh�6�e(i}A�[�ʹm��5ʀl�p�%��c�ap�������6K�;6o���,Ct�����sCE\��-6�ЊP�a.��T�A�tH�`��X�A0SO-�P�FYF�$<�C�}:�f�[|���������9�ÜC��N�÷W��9U�Gp��c�dK:0�3pB�I^���ͤ�S�6�z�'�_�iP���Lھ��U"{n;�g�A	Cf�
7��$~=zB�;EԇQ�0&!�q�Mk&�����Pc�ʒc�ak0Z�EL2���)�'��lZQ-�,��r��\�	�(C������@0Z�
��1	2/�[韑��U�{E?Ł�T�>+a�ˊ�� �P�)uO�OO��ƦF��8�y	)���*��r�����]��E�dytl�L_ϸ����fّPLX�x�)L��R��QC�R��Xbض&3��,5M�jF��b�8C�� �ʪ��B���#�)���Eg3_xF³x��x�k	�˷��\HJ�İY5��ڼ�/jsևE����+e2HYl�tl|,:|�B�<�0�
MK֬I9���s����:�Y?�j�p6W�q�6,��;vR6T��.z��J�
����v=h,݇ȑ��@)���o�ၘY,F��8�^<,�	w���'�=�&I����m�╱�r��S��5[ٓЩ�0j�1K���3n�m�	#E�q�4���1���F�Y�o���4bMm�T(G���@��H��C���x��W���F��F]dh.�݀��&}Ehd4V�r`?٢oѻ�^܉��-9x��ȕ�s��I��q�P�kuD	t�zтj춑����g56:y��.�_�!�x1^Am��Bm94���9������尓�ݫW��k����^����?m�8D��n��TSeSU]�y�D�C����Hgf3]C8@+щCt���yg鋻1�̻q���_�q|H1� �a|D3���H"��R7�����'�T�$]�\����˃j�?>����)O{�z��� 'k'l6!G+�&`[f��� �Z\��T�3ֵw�F�$R��70�f�I~OA��<��O�I�C)I�<ف�b�m�)�پ�?�^��s��i�ހ���h0��f.P���u�����[�I�Θ
~�u��q�/@��=)��*����ћ�o
-~�zC�?�*œK����|�����������܀��[��ũ��2���'W�4�<��^+x1�������.
Ք�:�Q]İ�z�z��=q��J�������aT�"�G��PK   }y�XR(��  c  6   org/apache/maven/wrapper/SystemPropertiesHandler.class�ViWW~.$�Q4�-�$,q��*�J�Ģ�vH��H2'���n�M�jw>�� ��S{�������ԾwBHb��_��y���˽����g [0�B*%�d��`XzB9�������a��ѣj������3*�ɰ�J�jܷ_I�����RͰ��i��;�ƹ/�j�IS�5j�DB���X�P_,��>)�55̣�bæ�@�ܐb��G�s���Pϰ<ᐡ'�a�<�B���$��$��A����*�%��גi3d\I0�[T0yc~O�E�;~F��5���ו��VV�3�B3�J�GF��N}){8����u9+}#1C�T��܅V�+��W�DB�d��/p\��yn/t7�w:̭CI�dX�WNk���|Ê<��T��޴�pÅ.T:�����0t�y2Ƶf��4'2�,��)���؊mT�J�p��;#���\��/L�&�E3[����у�NS�2���JUH������-�m0��WF/ \3���~[�sa�/���؇%�я�(�E\�UMn(�n04�����@ �21�PW*� ŔT��6�>?,4�e�0BE�Y���(a4W'�=��}�(Uxc�8��t#�S�J8�}	��|��H��M�L��w�~E��9T�32�7dp���Q~Zt @��c2��pQn�Sb�Qf����q<W���t��bRl�vsa{c��'�\s����@V��ĠJ8!cB�[�f g�v\�"B:!C�N�5�t�����la��!!%�D�h8����ZԌYI�(L�8-Pp
�J<ͫ���𔌧�LQ�P�H�	Î2�q�n�'9�ŕZ��Ѯ�.�G�����!�Ik�ߡuˤ����#�VK�Ӊqn��Ρ��a%>����'�̘J7Ȧ���4�TO`��eXWv�FL�E�i�Q�Pv�30^�ˏS�UN�)q�l�|���wї��h�]^���rSVԖ�ʕ,�������/E��J���/^�b�l��Z��sǙ:Oz5!S	O�����aWtz����3mP���EG��	���K���6�<����T_��� �k7z�
�˄�݂ki�V�;�n\1FiWI{z���e����h�{g�.Ӧ���a[�*}� ^��V��IR�\Q�ڀ?� �͠*��	������~iuD\> �W�"�љ���_�B���h��O}�Gmn{�ݶk�hΡcl]daC��6{3؞Aw�v]��`�,����8�p��<��vۮ��6�G2x<�,���Y2m�e�v�$ϣ�����,�YLU���͋���h�6z��`7]�����0�t`O��m��y�@��t�V��H���7���.�бg�|���Ľ���\����0Y[O��"�J�j��x�v���=J����>��vRTP4>>�G�:�>�'��5��O	�
�#z#�Y��H>+�9I}A� \7� ቛ�J�I8O[	u�1	�7)��B*�,��OT��m��*	��1�y��*��������**��g���p�U��[G*�X���;Z]D=H����PK   }y�X��   a  '   org/apache/maven/wrapper/Verifier.class�P�N�@}.%W
�&F6�$,�*�UAY�#rӫ�%:�~���.:�0X����������a��6�I��L�N?yS��u��N,{�:�=3�ѹq���8�N�O�ƽ������?$l��8x�.�u^��`\HL>��J���%D���0���:[��Yߘ0����� !4/�S.�o�M�Β��V\�}ʗb(���|��F����UD��T54@�&���.�%���mPK   }y�X�W!  �
  3   org/apache/maven/wrapper/WrapperConfiguration.class���V�F��!��U �s	9PJ�ӠNM	�����>P�&� ��eɰ�3���*+Y�����G�-���G{f��g������ X��at�N�
���0�_p���5{�V?t�_��3܉͖B�axj�NT~�Ou��_�zY���zM���M�<6N�5��ك0�ĎVJf*{�Z.�ܭh�=m?_)fr���0��OA?Z�W�������L,ݴ���du��TQ�0�`#���Uչɠ��7�aY�r/BS0�O)�W/��]4i߿0�r�1LJ����e�Y-!��+x �����uiV-~�#�N[!s�9�l[?;��|���U*��vv')r�`�I�#�&惺�{��:C��~z1�xO|��Sm��L��U*�0�Hni�t��H����b!��TrZa�a�+a�zlTu�yU`�
�Z�ĪX|�`A0�~5���[R�,�܍�W
��kҽ9	������r˽P�&27{�O����|��'��m�d��p�Z�6i:%^��\����:7�#Z�O�������Z��Z�5C��d�sj�j�]�U�ɰ���p�Z��m�	�t�ʳ��wsӐ���ݚD�x��V���V�H��GT��zl(&�{�l˜�7L�Z�?�H����n'��Gc2o�>L��^��a7ފ��_� RS/��m��y�^;Է�㲣��xD-�A�o�ޕu<�7du���o��	����~?�G��H��隹�o/���kG(����uE�ƕ�;�x��=>y��5F�щ���xt���?�Ɯk�{��n���O �u�!0�{�3��x]��۲I��W
	$id��w>�J�X�����I������F�6R~�:y���)��.Q̲�a���3�/��(��d4�\
�R2Y���6 �>H;��� �$��-�'�X	�$)f���%Q8�W�� HJ
���d:�C1�6 3n�3P���A^IAJؓ��A�Sj"n�(��T�� Ⱦd_��|�5żi��+R�*��R��,At�9n��+�T�� �!y-Wd5rF1f�5_�7nL�PK   }y�X��e��    .   org/apache/maven/wrapper/WrapperExecutor.class�X`��~[���e�8�0D��BH�;�DX�����g�,	�d'@�-�{7-]��J-��(�@K-�t����~��$K� ���o�����#�}�^ ��U
��x��2F��-Ƥ�h�J���n��w�eX	>��a�{;;��#Y;��c��ɥ[�Q�7Z�J'��-33~�j�]!x�1i�$�d˘7[z{���63���I�QgC�H���촕om�ΥiհP�(g!k[�ނ� `��%*��h%,���p�` �8J��Õ�F+�km԰Lay)A�J��I�V2�
�a��cp�B]龭|djI�c��������]��	hmة�x��g]ӗM�քٱo�t��bN�p���8Ia�!���g}}==���|�Տ5
M^�C����5'eքF�҂�)&��А��hӳ�Rs� �&�:֢�d�$�1�LO����\5�~W�\D>�wpN���S���>|�5�ΰ�P�oI&Ƭ�l�p��k�5g�8gQ�gY�:�F��d����T�zXai唓���h�f����%�����G��]G�d�<'J���tl�|_O��5R��ulG�*��f�H��I[����@�b ���3햁��V�G���*,ʘv1�p��A?��e�h�f#c
�~d���r�u��j\�c;��3f���~X)e��X2�.3W?���W��c7F������pE�$4u���~;�*�X.ձ�9�!t̒j�Qh�`�	��tBG����J/�JĴ�~�����| mY�#@�vZ����}��@�0���E�y^���@�>?�*�=z���5��� ���N��N��Z��u\#r���V��:jaħ�����vo-ހ7jx��7�-��Y?nN&�������u�a�9�t�`���:ށw�9k(ch+"�N���:ރ���V�!:�S��>���i{r*!�+d7����-)��W5|(�8��¡��G�[����޲94U���	��
�~T��&��#�U��tr��M���8n��	��-�ȿ?c��$��LL�k8�_^��q� Ԃ�i�:QM��܎;4ܩ�.I@�sx�RN��<aJ�}V�ݒe2�K�K�B=�cFH�{c)s��F�����8$�klJ����r��w�u���EE��t|A��:�r�}Q���+�1�hb����!VW����V|�L� ���H;=4b���bI!:�J'�=4i�-�xH����k�:�K8��-͑=��m��G�-���T��=Ԣ��q�8�cI?���Z��'t|O��O2;,)�~���5���q�z�S~���Ñ���x-��pgg��ߎ_����Is�'H�k�V�����:~�ߋ��澞�Ô���E�?Ɏ�r#����:��i?b�~���-9��k�w��CR�ƸJ�G�O�����tB�9Տ����H"=-��-�7���a`g[�gu<��	s�3���T�4X[[_T;9�,�\)�C�(��*UM�*�O��N��Q�4���/mv]�]���x2cj*P�N��Z��j�b#���eS�D2�Hp�Y��j�T碲�7���Q����R�~���fsg"c�$t`���N��~�m]�V�PGkj���Q����YaS{#%��M��j2�%�[�l�|Dˍ�B��
�j����K��.+�!U�S�ں�đx�M�\C{��n��q!���t�tF��kV�w���:7�w�t��{�zz;���
��b�^��f%�A�̋L	��m��"�vv���{�:�WJ�z���U&+[Y^B��6̞`86��j��h��������SUof���m�vf.�,t�\>ڭ4��4q������O�M���$G�E���ى�f�_r\<(��A�I�4x:� ��1�-�,`���2R��o}�Bo �{,6��/E�j&s��Yr�;b21��|#�33�lz�B�^hAAtFS�֬,^��'��+��"��H}E���b��)�]L\(�S�S�/����ab:f��b)�0�aO��g%3(Fz�A�� �9��
�_�xޤvWĘ#\_+*�#�ٵ-9aJ��J��G�1#��vͤ�ĜL�e��V��*�~a �B�V'A��9��S�)_�q�h|�E�K8n,��8R4��㦢������"(Ws~���y��{j�Z��pԆ�e�P=��9���₡i,�F��L���<g�&��O�%Q�9�k���4;MY;���,���Xy�݅P�d�=U7�f�8�S�s$���vD��s}؎cx�?��=͵G�.��N�k	�]�� ��p��'�w�e�Mw��1r76E\Ŷ�c��lm�,W�љC���@^9Ħ�#�]��%����9�#2��&������C3���w箘C�Jw������׹�k���9��S3x[�rg�ξ�f۝��]Fğ�Ӹ�;��x?�=O^�e|�O�l�"l�Q�����4��W��}��g�{Џ+1��� ���؁[q!�g;�.&��w�O�����Fr�!�o�z�A׫3ԙ�O:�RΗ�b����z���5��ט�J�W��^�I�*�k豏���nG�RwCAX�:Kҟ��id����i�f�[��>�0Vұ��3�s�9�~�h~>�{����M9��>\}2}~9�G8�Hߘ�7������X�T����Fs�qy���9��~��W��݄`���z_����7�<���������!�>�-��fwaO��-�n��9Fk�Y�Rz`/��q��ŀ&��'�)ZC���Y�L�k
WDn�`:\OncL�a������V����M�lΉg<���a�M��pj��j�?��"�b�j��̂s���6�ߖΐ9��������1�,�WU�BWp������� �K`O�����4��߾�����A�{�Y�������
�B-R�Z���ķ���k�z-ׯ��נ�;��Z����v�)�x�*_�׮SZ�{�d^Rk������b��'�Y�i�M��/�/���|q�߁���I�(r�ߋ�R�m}�G_#���7��;���_�T�lr�麈�n��N�*�0��T"U��j��:R��Ku;^��K� վ����@����g@+��~�1��{�&|�����@M�y�?D�rM������U������ȴ:�����$������B���k$.���D�W�)� �~)'�<��A�ꮒ0:J�s^Ty�]�N��`{�'P�?�̼���PA����R����PK   }y�X��   T  ?   org/apache/maven/wrapper/cli/AbstractCommandLineConverter.class�UaO�P=�ut�!���QPa�HP��$,K�fW�(�ڥ�@�����D�?�x_[�����@���}�{�}���� �c[BB"�(��g9��4��Y�c���]���i��6CH��DD�m��jeM?1ԒvfX깣�ˆ��ESݵK%�:Θ�q�9Ñ "$aQCQ��0�fG��|�u4�m�ڵ�3�q�a@��/f��kr�71�x���(0��<˰'g�;�'-_4R����qE�w�0�qL0��/���"[q��s�E1����q�oU\��I��|K�4W2Ð뙿���Y��n�o8@a�>&��<}P-�'��f�غV�i��߃����T�:�L�a�&�E�����Mg��mo��è�)P/c�6�M�Z�u�ID�tC��uL����Ս�k�VE��]w��l�)TK�����]<2��zw1ٵ�89wg��Sɰ��M��QWSN��ҩ.�6���^���}Maُ��٭V���T�C�2�ґ]utc��?�i��9�0G�<����a��i����Pd<�\�}�E������xJϨ_�gxNq�Ff�"]Fȴ��]PQ��	��>(�$%�T�Hc�u�{^�"��f<�Yk@�Wq(�0���8�8E�j�^���eZU�Ҫ/<a��C�|��,)5�j��ljv���Ku�����mKs3ߺǷ�}�(P�F(InD�����;�@].���B���U�'��%^[#�'!O��\��I,�įM��j7�z(�oџ�J!��7PK   }y�Xm�v��  -  I   org/apache/maven/wrapper/cli/AbstractPropertiesCommandLineConverter.class�V[WU�N3a�0���Jm��D���Z����p�� m����83����껯��Z]�����A��}&�-��M�b�9{����߾����0��%��O� Ï&��Y�+e%���Kʦ�ǷL�\V�x��ŧV,�Tr��Q*)z>��괡o����M���ٷ|�HVD��}]�T�EE/�3���	ZD�2Zq�a�D�L�D��Z��v��=;�e[3t��H��x�[o���Mw{ͨ��ռ��pADHF'���x тbZ��!~��I�T_���+"zd\ī���˄��XSf�RRu�b����d�r�.6��ʙ�3�P_�qWΓ��f��"�)ގ����wkmV)K�OB"�<n��r��?`����z��16���!oa�a�,J���R����n>;n�5�N�P����q׉�ïU�b����N�\9i� Q:)�&�l��-"�qNݶ��~�O�#c
������u5g'�a`7%��#�]�%E��W��WB��Np�-HcVĜ�y,0��C�0�r�L^{-z%`q<���>�kUx!㜟T�a����9��;$,�x��-����-[�s�M�('��ޏe,�"#Wk_�S�X�Nm�
̴�'m|�\�������"I�i#�����+�5�,y4���b2�L��j��I�G����<�,�ϲ�H�N{�4G�J%���+��Z}�&�!�Jn%xbD"K��S����Z�S�A�)J�ڇ�:�7��^$x��s��p�wʻ�;���[�m�	W���Ω5�"v��8���-�;���?��	�8kl�g�wL3��Ѭj�y:��T*��y�2lT|�gr�;(e���S�h<�ѓ�� �%\�?�f0~���cZ�i�n��3��ir
=�aV�)�6 �< �B����Gڣb�UZ��>����!.��T�Km�*���*^����R���!��
�p��P}��w�ִ�8���V��
��i�wv��a9��O�[z�7���*r����s�ٳA2��d���͡	X�r�U��x�9F�h����4y���	�[NW����O��e�9��a�[����W�~������}��1�Np�]���>�GA�F�N2E�%���ԣ�iQQ�Y�uk�{�,��:�-ÄE~�{�rI�$�4A�"&��W ����?�Ֆ�a�%��"��'_�������>E6?w��GR��^ ]ţ�4H�G�����KY������K|�O�PK   }y�X�:�dP  g  ?   org/apache/maven/wrapper/cli/CommandLineArgumentException.class��OKAƟI�M�,K�(�[i4�.a!��!�������2��}�NB�>@*�Y���C30�����3������%l g#_F���2�3���T&"��.�J�ZH�t	j�η��(!���Р6�H�Z�X�&*����u��-��Py�E̝p�)�t�XqE]_�^L�!���Ҁ�de�|/s��I?�����AT��e��)a�e0�LDL�q�ݱC`<�����uO����X'kk�LPꇩr��05��ua h¼�Y ��Y�^W�&n��s��,��υ�X+O����B�
v3��=T5ð�K�=rzv�}>����E��XȾ`�f�}d#ֲ��'PK   }y�Xlk�I  �  7   org/apache/maven/wrapper/cli/CommandLineConverter.class�R�J�0>�������7�.4�
���L��в��;֌6-YW}6/| J�R���z��|�~Nr>��? �-(8KdHYʂ�1�Q�W��%"N�I31p��n"r�J�C�31�������m�~P֏�i��9���[�i�1��F�z�872�(�)�@��`�X"�cS��L��$��q���r�����8]�Ly~���g�OL�p0ӯ��rym�[���U\--<�����g��;�GfF@+J��Ss��[�:ƹ��G���4�'V� ��|5��B	,u*��*�ծkY7 �4 [i�m������=����7PK   }y�X��I�U  �  4   org/apache/maven/wrapper/cli/CommandLineOption.class�VIsG�ڒ=�4^P�؃	K�`dC����,�����IFR#��(�K�?�%��KI*6E�J�r�?����T*���-���p����{�����?���W ��u=�)��E���XK�YL.�6y�a��Mݙf�Ec�
�\���ɛZe#˝ ����`@� �NYv1����O����L>��r��ɼ�'�V�����n�Ų�[&�b��
�P���S�؅���T�`7�`�䲥�K����L�"mh�JJPڣb��R5�X-q�YzV�!��U�����m-gp��.��e3D�1�9)O�0�#
>Pq�vP�T(�����:n�ք�	"J�MW<����zJA��Y���b'1���$���D�%cw�.����N�\��^qDvΩ��y�`����jB�dZ=������Vu��� .�ʟR1%�B^�ۺ[Oq9��2�P/�Ƣ�.bm"	�񪊴�x��yM@�h�Ij��5�q�<�,:n���Ŝp�1��h��� 2���;м0:6�[�o���O(I����ȵY��� ����7���ᲊܣ�q�����p��z�Ԝ�M�v�������b��ç�B�Ǜ͝NŖ���V����9[��r�^�5w8c�5cY�uq��>K�����i�3�6�-�Lh����J���w	~ �h�G�ZY�&����D�ȝŝfW�<�'��x����|Jΐ�;'I�@������pI+W�,�h�㼼�c�8H>gCJ��y�jPD��N	Y�&�0��������p����=��Ɛ�[�h�gR�ZU;ϯ�Α�HN	�8B��C_�>���t���ы_?ߧ?z����\�r���u�t{�)����;|��z9�,���K�o!�	�[�$^a���W8��hs��7��?#8�
���x�׏���}���@�N���aD�8H�s>�1|F���t������sh��ny"X��JkM�i������`�R�_!�M?�ȋ��]ƕ��0�<p���q�E�W���l��� �I"t�p��o��b�#�	V@b1�:a��q�0&;b��'1���mf�C0���2iGJ<���m���Qڅ����P����:�?%��)3�h�ԄX�qi�֙7�&�1�����m�\I�o�;�4�n��Rw�9���f]�m�T}.�Ij�%�&5�u�-j�N`�Zj�
q���g���%��T�j�UOh\��P*pdS�?��`��#���j��M�en�2��K�d���&�`����p}��~��� k��y�9,����" ��!g%䐀����C�P3​��s�3mo�ͣ.�e�w�?!�)q���,+{�)��}ֱ�gZ����_�Z_�PK   }y�X�#�ر     6   org/apache/maven/wrapper/cli/CommandLineParser$1.class��A��@D�k4�f5�Y��V�†(x�?�OL�t�nu�6��V����^�
�r�?��#E�0q�Pܰދ��$V�yn�J�R�]]��W��-� �{�"!d�X����V����������e8�/���ƅ�k9�]��O���0\Z+~n8	�m<�F'��.��	z��G(:*�� PK   }y�X�@Ƀ  �  I   org/apache/maven/wrapper/cli/CommandLineParser$AfterFirstSubCommand.class�VmSW~N,,�	T���=
$��[���T	��>^�mXMv3�E�#����ؖ�N�u�����I�XR��v��ᾜ��ܳ���~��g g�$-bHP
����u톺���~��M7���g��FC�;e�w7U�r�L���W��U���l��e���e�3S�g��z���N�Va��3�0���]����q���4c�k1!�4#/��*�R���\)��[��[��xF�r���\�3�blg�w�=e�E^�^W��(N`���N���+\�@͍gf:Zu�����׉��%x7FBx�'��o��~��w,�`�O��#���R�o��p��8�Ot�g�{ӯF���b�c�s��d[w��o�L��]닪�A�H����4�˜`��1�ퟱ�Ә�^D���������rG�_<�d)��o�Ҍ��ƶ~���L�G�o����.1�oW�[\_��DA�^w��`�B��R���4�*�: 
vߨ�f�v����
TaM�}�:�J�g�����4ݪ`�����������%�V��y=,�aI��
�X5�w�ӉN�Y�p�U�w�R]�Z.��x�g�d�V+8�W�������+��W��y�qA`��/\��{��\����"=DC���{O�i�N�j�>�8����~����}#��{���OP8�y��A��QY�"i��QK�h�؍I;��Yt�?�9�稝<�0����'0~���q<�_0�1�+&pj+G��Yy�����;��Vn�����̹G��udڠT0.�8!��ī��G/�+�.2����Ʃ	ڹL�0�2�� >�\!W�� "/Ҧ�F�+�+�Xګ/ڐ��:֐_m�(�K�W�6�W�5�aq^I&�I�:�x )�L�X�&��ly)�Sn���η��PK   }y�X�wM��  0  A   org/apache/maven/wrapper/cli/CommandLineParser$AfterOptions.class��kOA�߳-ݲ-�� �Ze[/T��JL*��5�ڵ,��d�\�O~�x!���2�ٮe� Yi����s�y��ə����O �1�AADE4��۩�bU���.�K�p����嚩O����*E�2��0���]�y�ꚶ� ���#L��	�+\a��!�N�T� L�T�l�]���C�owϳ�N�EW�H�)�\���!̄���Z�����~4�qL��8��HqY��&��>�:�U-�66)�}����ㄊ�$Nb�0N.�KT*�7]G�Dm��}��`�D�N�~��^�k�E�y!kl�-�eQ+	ǔc�u�L�ٻawL�!]��6��[ D�S�b�^�mkbi�cn���ޖZ�t���L�֬m�U0��&��9#tU��l#(�9����ڼ�攍'�̒��9c2,��S�2��h4����ay��a���DK�$W���	�����.��6r\6�|�pj��ɽ��uS<���m"C�P3ٯHn�P�i~v#�7��-ΰ��9gq�zK��yz�[9K�|l�䂠w��b(���c�|�3��Ց����l��Ra�=@�5���)({���p�
F|�CϚ�f��/�C�W.hqZ	H�Zd�IN�8���G򚖊2�m����
c-N/l���UO�5�s:��r� �_Q��������&����PK   }y�X1�GX6  �  J   org/apache/maven/wrapper/cli/CommandLineParser$BeforeFirstSubCommand.class�VmWE~nXXM1`Q��@$���{��i�P߆e�&���R�G�࿨G=T9j����?C�wv�J�l��fg�ܹw�s������� Ɛ#���Z�l'�{�ܕ��x(�ā#����0�ļ](kg%g����='�َ\�9E7��]�'�M欜;M0�V�YL���)픭J�o6p�f��n�؝$$���P@4b����lk�@8%I���q����T�urg�UZZ�����H䅕M�ө
��ކ�0�h��f�M\"3AӔ�b��$�>8��~�n.�X{�F���u�c���N�j��%\-Gc}��4���E::�C���2���CW��mK�U�̀I�c=����N����#Fh4���#�O�0z0��5H����ʻ���⬎a��g��~AZ��#Sz��c�p��sg	��<�vڞBgoO�wH�(!�2�¸��:�7p�'�}��
�� �����{6sKZa!�4Ih�`EA0���U�XaLaF9�%�����o��/�W��I���>����~a[:��v�%-+�)��ɩqI��׊��W�W7�l\��Z� ]��&�p\A��^����Z�_��ɪ�OY�3b��c�e| L��͗9�(��A�������^�TǶУ�j�U3B	n�_����L&h�)�!�=�ӲeIg>/�Eɬj}E��V������%�%�^	�^�6��-NT��E=1j�|S�๙�=��S2$�u7�"�HD�ܫa	������C��߈�����;�0�m���1��54�u,����X�X��,����bG�#6�̭r���% �М��!"k��?�?�=���<��k�0싯|��#�d�<썱~���2-I��dj�O0v��ߎ0�Q+۵�<�bT��b��	�y
4�.�� M#E3X�YlЂј��I|�[�u�[�^���*��n���h�؋=�'�l�q9�q��R�묫�߇��!�S�i�s�U^8֑��׻큪zw��%�S��g���CqI���Dw�'+�Ϲ��B���s-�W<���ۚz��0Y������PK   }y�X�w5fr  e  T   org/apache/maven/wrapper/cli/CommandLineParser$CaseInsensitiveStringComparator.class�S[OA�f۲�le-E���lA�1%$��I�rIJx�Clg��-�W������g�1��n�P�41��9���?�|��s����v���N�;�s�Xx=~*��^�~_h��J��z\��R�=�C���<
�H��V���PX�k�abS*m1����&M�,�{�O������?�a
�������b2��1��haJ3��WCk��A��`��y�묃9�3؉�a���0m�f���Hv��Y��A����dv�C���a�,�f������c::�!�^���F��~$�n
�P�poPmyt���V��wۼ�twi���F-%�t�Et��;�����eq\R�����ʰ1~.��%;�G���?u~ea����"�\+h_��f�#��L��PJ�z���mxk�Ͱ�MڠU���}O��=�4���v�nIF2S��)�߸aeq�tg�{X$��+I�5E�I*���m-~Df����YS1R�DXS���<k:F-3T���R\���Yԃ��xD8������7T����+�H�˕o�Z�s������'���Z�s\��i��5I+�~ �À��3XA��)���2*�\ų��E�5���M�;�PK   }y�X���  o  K   org/apache/maven/wrapper/cli/CommandLineParser$KnownOptionParserState.class�X[x��ǖ��2	F�MlJ"Z���E���qc;�N�:В��؛Ȼ���N���{��@k��Ih!@�X�q��B(�Jo�ח���/���k{��J�%��_��ٳ�3��s�����^|@;���
e(�mZ�15��G�ب:��qKM�4+O걝��j$ztCۯZ�f��5�qc_��M���wTG(߮��)���)NeG���<�������7�
�]�y��7�)+��A����ݮkh-�A!H�R��Cv��-K�ׂhm)6Hu�h5
��<������'c]����m'�
T2�5��޲�%��ZZ���Lk�@Eϥ�<��G�J\�Hs^Pn^'X�:����yOq1^���Z>(�!�	��_�|EU�$bU7HlD�@��`�r��I�hD���a�٣�]�pzT37��l{��sw�k�� �n�G4'iH��cd�{c$ajv�0����"�Q}-
�(�n���1�7tD�?ڱE�V���߳/�۴FU���'�k�=�
;
G�Ќ���U�hi`7Kt�#d0��Έ�� �( ]FDM9�s����v$e�czBKDa$~����N�R�[�V�F1YHu��&y��+9����(�
�L���JjL����呤}
�I�g����0�+�4�sܲy���g}�n�fw)����"q��MA�L�zqp�#�	��+TA�+�vs����>s)$�N|R��$��a��e�0*9j<��vݦ�V��ۗ�,�K�!�$$4�}%v�4���,SN,�nŖ�+P� �?�9"�sNl���(�
F%��K�J�#��W��!�������yP�K��t��)ws��~g�;G);2�YZ�eLb����Y�cZ�@u�������J|����P⎨v�v��_��"�D�3�C`M}Ca�b�~E�\XWRԳ�c'�#q/��FNإ�,-�2�e�,^�0G�	|C�7%��oQ�<f�K�-��}��!�fl?�qZ����&쎴�LhV��2��h~}@�A��v6ePuo^l�)��v��IV�P����pٙA�X`m�$�I��'*5����)G%�����
�8�Q�\d�ċ�%����Y�*�kz�gg��
��8��nZ��DA4�>�����E}��͏R�_6T'm�����^h���v�	}��K�i�u(I=�3�&TK�w�3�z�_�0�B���^�1ԩ���������]P͉��P�HQ�h�������Z';i���{��vʭ{Y�o]Z����w��5J��r���6n6s������c�ʹ�nՙ�cZ�7Ān�Ь�Iնy���ܵ^�J|��`��?���XԴ},zD+��%�r���HR�ou��e�.U�.�x��<SQ�wnwf	��A�9z{�$M��)(Q1���9TN#<��h�j��S�.Z���њ�)lx����:(�x����J<�Z�֋S� N�Q��s�,e�����w�+1�RWb�Wb�e�"��&���@OQ��O V�͠�!\;���p,�6�F_@M�6Oc�+�l�~���3�U���Q���hk.���'��'��~��4nϠS<�n��jAn�}���%9����H~{}ć�O�sټ.��Ir0Y����X5����s��f?�T�Y��Ϲ����7<��K�o���<(�[;�]9����}E��]�|q/����.�w�A��9��3�����YU{�i�-�#�p8p*�T60�⁙EZ��i��l	�8������������������s������28��2�a�G��I��'��{?}a�|4�3��9~��� �WQ).�N�F̾Hxw�71.��I�6&���Έ?�u�g�CϿ����o�����f��I��уY����Nb�/�O�5(o|{}+��7jo\K ��T���pآ�M�5���i�g���kq�7.GM�W�W:�g�į�,������q�����U�Z\p-��߹��	�NU�Nz^D-��&�F��������=��vo��PK   }y�XmKs�  �  J   org/apache/maven/wrapper/cli/CommandLineParser$MissingOptionArgState.class��[OQ��SJ�[( �xD�Y�"Z��\��	�eSִg����/���A��?��8g���a�4ٝ9��������O �&BGa��,����(ozM�RߵE�n�z�j��V�&�fɔƪ��}e:�)++u״�S���
� D��t���R@b�p<�lv=��Q$T�IB1 �F�\�$Wg�R����Y^��gg�M�ߣ�}������L��zUȊ���,�l�m��⢆K�Lxr�섄%9�^5�3��m�x+1\�p�)K����զ�6������a�^s��ܮ�%��S�,����&�֭��k��ZlTy��d�Eu]ئ��Ű�e:���9��0Ξ����1�;�]Q�O5JHZrْG�i�؏-�Mޛɮv�����"��&�c�0�g�fm�ec�TR��W��R�|U8��D1M��o�?&^O�ەl�(&�v�	�1��|�j���ռ2��s�B�N���\�;�}vC���.t�N�� A.Fym��[�x����χ}h$�?@�K�����Z8�&'�����y����rߠ�B~� �_O�>x��F`��q��b+�v�>��_`\����'�[j�7��f�y�����Q\㛿����}�pZ#��K�O�YPц`����X-�
6�	�,�;�&��S�����.�>y�0V���~� ��PK   }y�XV�{�  �  K   org/apache/maven/wrapper/cli/CommandLineParser$OptionAwareParserState.class�UmOA~��^9��"TŪ i�^)��Ҁ1$�i�߶׵��w���C��/�45���Q�٣`	rD���y晝�����O ��Ћz�5�A�a�v:osc]�-�),}���pt�i�%���V�lZb�;�p��ڞi[[��h*�C�[7�t�!_汨"�^��>hs:żi���ى�a�������Yq$���SNd���w9%V!�k8����ap���or�ɭ�^��j3/T$qA�E�0Đ��������U��ǠTU�pE�U�0|ʡ1��0��g��3AL4\g(ܗ���p�L��*��Y�Ҳ^��$�V����`(���n��*Ƒ�����&d㿧R��Kv�j4.+��p��Z�4�e���*wL)w�a�Wett�*2$���n�I�[��M�-��&h�����4dD��5�Z�����G����0F��2owR�V��K���V�$>��Ĳ�Sjr��)y\���P������%��]��a�Q�sB�H$�C����'�2ERY^H�Od'�������6b���7�{V��(T�F��!G�$�ht�$1B��qo��C��4Gf�(����s��G���r�w�]���Y����"rV!�1r5H���#���5)I�����:��'��Ĕ�XQ�w��q�;4V{�P.C��}�� Y?��a����*����R{L�!�����C PK   }y�X��`�  �  E   org/apache/maven/wrapper/cli/CommandLineParser$OptionComparator.class�TmOA~��zP

UPD����
"�VM�T0�!�oKٔ#�]swE�?�/~#��?�8{WAڪ��fvfw�gf����� ,�E
B*�1Dp�aӲ+����j�X��������UC�[�7��)�p���n�5,��unsײn���n0�R�(�����#~̵*7+����(�Q� ��7�>�3,�4��!Z��;�t��p��V��b���eSrmì\�E	���R��ά.iHⶊ;1�`�a�
jުV�?�P͠ĶR�N~���6���o�R�������>�N@�y��1\�X�33���=�a�j��	��T���nId�=��3�oN�_)=��tAE�a���u@��偝Fm_�o�~�,�E�̫{�6�i��m;hC�>�h�<[�!4���9BD���h�x�}���4��ݒ.P��u@�yM�¦T���\���]2*&w�3��	AK=ւR�A��%�a��+CV(�fAb�LS��*w��0~�{���ɗ�<>T<��%�a��z:]���$�N�ViZi$����Ϥ(���tCa�%=�o��I2� ��?�e�OgO=G�;��_���\�i5|�1��	����e�>�H!��1�D�����ͨR[��J-C��&�E��N�OHǷ	�"�$�e�н蝙�@�}��:�K���`K�u+�Y.���}���
I�I�LW��p	<B����3o�
V=�c��W�ulb�"�c��<�a��<FPK   }y�X7A�ϳ  !  F   org/apache/maven/wrapper/cli/CommandLineParser$OptionParserState.class�SMO�@���$i���~іC�Dm*�T�RH�r�1+��^[��?�kz��C ?��['Bpq�e�=͛��^������(�\���*��Ą�HEp*�X�K��0"M��H��$��>(-��ɤY?L�J�Za%��Iiew	�NwTG��? ��Ĺ�#�C�p|&K���	,8��I<��G�Y$��F�(WϚ�=U��྿�#4���ya�MH��t���t�'��&��|������Ck�{��o��~��N�8_˓i$]��;Ewl��#B�b��]|����	��.�}K��R 4�i-M?Y&�*^6��",޺�*��?S�����͢	xk�zjp_�� p&\���8���?��0���I�h0oLx�&��+@+�E,���s\v��S�g�K3s�ۼ=L�s�O�\��g��(3��^�_3�q�V�PK   }y�XO��4�    A   org/apache/maven/wrapper/cli/CommandLineParser$OptionString.class�T�NA�N/l[���\TT�v�l[�ZP#��A�b�߰ݔ�v��]P�'�/$�&>�E<��"���!��˙���sf��ɏ_ �4��
b*��!�����0�M�.�L[��F�tu�f��N�.�ʊe������Z÷{�w-�J�q�)!��#��^vUo.�S�(�"��[�l�L�ɶol��6RPѧ�_��[ֶvL�'D���%HT��	�|>�a�(��bc��-�)��ｷ�m�PQ�
&#�.<۵j�Ma}���b78B�4�B�w"k3�4eIqK��		�i�̶C�	�(/�S�IH��[^�&����I�-;�d�Vw�[��Vl�LY ���Zrc����V.pʄ�u__�FH��_E��i��W�sA6Eٔ��Y��0Lϛ*.��M�B�w(�ug�5��L�pü�ԗ�m��5�y��`�0ߝ�8�E�B�ۄ��J��!����2�m�>�6�� ��lS��h���f���At.�� �"F1$)�~�A��M��H��`$Ex��9t�s`Q��t����b�'�7�qUN����;��om-�>����h�)�C�@���u�W�~��kA�
�t��Ƹv���itM��3L�&S��b^c�1ɬ��C98�I ך[N���+�$D�p�t�p�MG�ߑ���PG� h�QP�e,a���t�PK   }y�XX����  �  K   org/apache/maven/wrapper/cli/CommandLineParser$OptionStringComparator.class�T�OA�����q����ł-E��SB$��&LjH�m)����k��O��/�hb�տ�g�� M�fvf�7��۟��|��u
"TQ�0<w����\V�	�z��FCxV�f[y�^��A�v�+���m���v*�mp���гa;v��I��t��}�!?�V�;kw�P��04�`���IH5�T�j�T�p�a������o���^�Ò��j��%x6'����8�V��l�x�M�Ӓ.�	�5L0���f`׬˽T���ʬ;����^��Y��n����g�}ۨU�gx�-��'�#Fn���|�_��hR~���co�n�+��n�7��]iK��:-�]�mT����E <b�|XdX�~F}%���)'�q[�Z���w�������-��;�-�CF�q���q����Bwe�m�����5��n��E���`H�Ӡ�N/����"�HF�Ρ}&EA���e��� L�>I&�������ʜAW?BS?A�l�_ޭ��c��	��i�D�ǈ��_a�a��P��)�D�ē�(�(�8��f�q������dX�J+i� �=�ð�-<"M�'g�xbNc�4��7�c<c�`��W7��N�NH:�!��|G�<�3�=�GOb�Ҵ�	/�)�5�n�S�J������[t��C��̇�O�J��a��,LP�,5#�Uy���PK   }y�X����	  2  @   org/apache/maven/wrapper/cli/CommandLineParser$ParserState.class�SMo�@}�8upS��P(P ih\ B�@B��JA9p�$��U���n��ąH��(Ĭ�V���Z�gf�͛7��?��5)�mX9d�Bx�+F�;��/Τr?k1I�v���|_�^�S�X�P�Ĵ"I�ʁ���!]��d�B�D�	w(T�=��n��*c�jUk�jc=�<
S�V�=�'ؾ�XKH�(7g��O����ux�w��(:C^)6�����L<^����d����B}�#9���(�E�zṛ�o���%�j*k���m.�6��ּ��@�>^4�K%P�w�GxY^��Y��&����/�KpZ������9���f�&�>(%uc(�P�6�j���qN4oc����gD(̭��#�/;k�/u����9r����O���I��6����� 6P,�b��l�:g�u���@��M)�)%��&�naĄ�q'&��]^'ܻPW�c~g�mM(��2o4n�_�M�ۚK.N��\���>�ic���D���([*l�l�x>�PK   }y�X�Ć��  g  M   org/apache/maven/wrapper/cli/CommandLineParser$UnknownOptionParserState.class�UISQ�:� !"�"j2,	���R�UR����L��d&5� W��)�<����^�xQ�~3�M�
<��t��M�~�~�y`w���B@EM���M��Hoꉼ���Ķ-
�N�sFb��煙Y6L}U�E�xb>3�m�Q�1,��[s�����p�	�c�OŖH䄙M�9�afg������T9;F�6��Hf�04��9��6V'��X��r��u��6����:k��J*:q�В>�!$eMBu���,!X�"�;��XΩ�E!l��\��u>?�\ᒊ˸Bi,OB��d��8�H�\���uJ.�"��K��R~C����.�������.o�M�HXj4����,�-�;Eq�Ζ��p���m��a;+��� ��=�Y���i�&(����?_ϓ\$�o')�Q9��a�0�83�f��~ߐ<Z�3"�!�K��ۋ9Q,��nW�58����"�����6�Ǫ��n��F�A�o�¯�����u���JnO,�X���y��U�e�Fi�{P��=D��=t��Wy��	���a����3���L� p�w&�;�.}�L�����]��Z����請�z�e\B}e�D�2����_�ߪ��c�e�i�������QO��Q�cmw}�Vb�g�"��v?u�Yt"������Ϫ�"jD���֙A��684| ��+������S�J�H�g��xV� $�r6�1��a�E��$�~wg
�8�i����Na��m	Q~PK   }y�X/���x  �)  4   org/apache/maven/wrapper/cli/CommandLineParser.class�Z{`�Օ��df���B � J�!IHHD@H h�jP�̗d`2g&@,j}�g�֢�>Z����"!�W�V���խ[���v��ڧu��<�w�<2�Bp�����=��s�=�wνɋ�
�l�/Y�6�0�K03n+�:��v����l˷���N;\��׆::��o�?h���;,��ٝa�Ŋ�C��a0�Vn�6[��P�Z�s�J-�6��`�����m	��T���S4��)��/�n�RLGbr���@y�թ%��$����[m_\��1�		MVuE;���d[1�rP�qN01^A��X�����H��d���C����� '�������?Z-��̴^U�U���i� U�5j~2���H��)��y��B5�����NK�p�i�#<8U1�Pt�{P�I�a��r8-!_˨	��n:�eEV�#\hZ������*Nq�"�R�nb6���*ˋR븴�����A�#ɗZU�8Z$a20�g�h.����B�X$�?�y��i���TkC� �f��y�Q��d`wӨݛ���j���N�ޝ)X:�����n����p$�����`9��jg�}�w����nS���hk.V��@��UXM���[Ŧ/ƥy�.>^��s�h�\�aMBJ��h�"��X~�S_6фuGP�M�N�f�'�6�+.4a�YP9L�c�ƨ�uyVw�͏p4Gđ����XU��� ��̃�h5цv��}Q��bId�:U�Ԯ��>�#�~T ����O@��^���߰��ԋL����+JA55х͂�HWs$�Eu�Cݦvj��n\̜��ܞ��?\+b�S��Jl�%.5q�&X�E
F�MQ]���E��3UC7�x�^_8�D9�*W��t�?賷�je���uj�Z���T:�ev�6�`�F|�BCA*��a�b�3������6�VU���}�T+B2�K��$>7��bk���f���h>�NM�|Y8b)܎)�&�0�SeZnK(����
���.��`K�t�>UD��7�b�R���1q/���&m
2��2:�Ҫ���&��%�t�J��M�Ha��6;�Җ�z�ďU�qK��΀��f���Cx�
�`[�]�c�ʛGM<��07l�{/5�c�W�ZNbw�}�����0�V7�Я`䉁�s��I��=�V���6+��g��+g�����ƳJ��n%���������ܐ��XD׳�/�x	/���D��W|N�@���Dp�{��&ۗ��^Qa����o�	����/ˎ˓�K��xփ��ůT��d{>:�L�r��K�K��Ѓ��E��=�n�}�k#�;HF$����X�JU;�&>�HC~��#8�X�J�_&�[%�J��v�%�ז��G
���_�W*ҩ��y����!%�#Wsm�����k�&>��	�5O������ʾYm�b�����fR��uzJ�,�d�25���SD�8n�Fu�nɦۦE�մ���vq���4�)9B�t���pn�ώ��'��-&7m�BI �Q���
u��2�),T**}Ƙ2V���5�i7d��P��V��0���ܐ	D�L!A����Y��qXɮR���j�݆�"��ҧRv�H ��-v,xd�`R,%
�O�L/�G
� Oy��B�����$��4��S����B)"4I�z�5j����F�Q�Cd�?��^k�`0-d��RԵ�n01��Ѭ�[�Z8�l�L�;K=r��2�tSf��3j`�� k0��gG�aۗ���vwq"����k�Mc��}A��lx`WU]�H��lu�ᆮ�f;|n�L��2�b�Xa���:��~�U1܃K��s:q6�p|l���Z7�O&z���d�E��s��0$j����K4�K�x���)A�nS���JT��?! FtyCqM�-%al�33+N�`�u39ۣ�ě�_�3eا�Xwg"�2�3Ļ��ֲ��g]w̼�i�.����a�	��u}ְOڧ�鼈�F�PJ;������h��Fү�T����0!Si���|��aIe��W5�I�8`�U��E^z=�[��ueL�&J^:�@%Tg�T[i�3�����n�Ba� {�@V�I�Lh���f�*ǁ�s����6$��ك�KG�U�����=In��&P����ͺ���w�J�Zu��=��%��E�[N5yL]��ՇL1GJ�"���bG"S�TTN��_1{���r�)95×3��}@�|%07�q�V���?��r�>v��iu�[��~?ㆈ����A;����/Q��`2�#�L��1S��X�2\��>�R�̧/ƨ�kC�%h�j�VĮF�`��o��ʞt
C�s׆�j�_p���)C�3���#�2.�!w������#1a�k�Kd7仂�㹃���LÆ�s����-V�N�R�	C�?�{�Ui��L��)<�O��sFBd��d.�Gã�՟���Wn�ƫk=~�V��g�k!�F��#����ń^���6��� 8�lu2�of�\*��O%�0.�|�R�Kzq
ŝ�ԋ����kqӌ^����Oj`aV�D�k��b2����,�:�c�,$oL�,M�S2cJ{0롤X�Vom�������ETv1�i�Ԑv6� [	�~�~ʥG?�6�c^S�c��C���{Q[ߏ�M%�ҋ�f�a�}��_쐗��Z�3X_�,�:{p��q~�z�w ��&ź�a���u�{йH�u�$҃-�.��_%�6\�	fz�y{p�\W����(Z�kx��P�^O�3�t�f:E�:�qR��:]I�I���( ���މ�~l�F��4YHT�Rf{]}�Nv`��劯���/�+Mi�)//��tg�w{p�"���W�-�n��?؁|�;a.U��j���ܜ����<Z�/e�5��t���HOR��W�Ȟ�?ۃE��HU7>�ӎ��"��-{�aϡ���-U����ԃ2�63�[0>L����l��\�Â�����܈lG��c��Oq^C�A�!����YF`����r��K1�1
/�F\*�q�l��҉+�\)��*�W��ދ��	� ��Fy7ˇ�E����1n����pGVv���H����R��vL�e��o���j̠ffl����X�1YΔ��񸹜yӈf=��9�b� g�Π1^7-�HVp̅y\V�ͩ2*��|��zf�x��4�*��y_V˗���$�ES52?�����sl0p��1��p����oQ�_�P���ޏ,�8%k?\|��8:���5�6Xs��B�ı� 8 "1̻��ߓ�yF�r2�0���M�e!_�1���-⎼֏}
&'�<I z��x��^���,�D}x>k��
��JGY^#1�c0��՛���+]11o*1�B13��Jy�B~�0bRr�9q)k�[�<*�t܍�^w�l��;���[�N������>���P��Tb�#x(�;a���7���ß�}E�\��Կ)��v.IZ���[\;���2��}�x�s��w���,�$�A�>��z�c$��$&7esF��1hQUiܠ1�Kc
�=2b����r�149�Bc���+����r1N��OX����`�j"���D���m؇���S���I�����Qd?��!�\<x����1�f����/I^�-xEnƫ�^�����M{7�x߆�Ҥ��+�P�c�y��/0o?�T����u�|Eӭ�����۳�g�Lo�%�4����jy��ܢ��y����J��r�l�b������|B4q�L��̈iҞ�������c1�2$@
���è��@�!�x�@�ğe��0�~W�Bb�~�H�8��?�g���A��A�	��c����Sm��~�A^���!ޒ|�ovY8�_
�z�zG������peI��O�0-�+T:Oډ��=r��R�9Y�eW�9��g�@�����>��5ME�&���R��
V��x��[,��h����+��b��XE�J/�Hº:["�K���D�ҥ�X,������~�Vy|<1ڐ9 C;�N��)F�Qu+k�
��,��)O�Q_�Tu�\G��{q^i�cV�S��x�0�_
����x�9���^��N�#%I���W9X�����M�����R�k����L�%Q¡Wu�&��=]߳�og�^�������+��#���h䎮�����|���)ng��gI���r]휜�3D������.�X���Й���9�Cp�U�;+t$~�A�0e�M�s�6V�X�)��#�w���V��\�&�e�̹����̗g\�8����+30�1��ܿ*y*��g���Y=R�
��G�����!��Ky2'���_�k4��r~]�|�N	�sc�op���ɒ1����C�i;Kn���|~�σ���]9r�o��6���y;�lce'���}'�w��n>�ud���܍	�PK   }y�X��sP  �  4   org/apache/maven/wrapper/cli/ParsedCommandLine.class�Wiw�~��-��-�`'۲l��M�LL�b"��v�2X�<D)����I�n�o$�7����(�t�֞��~�hz8�Ͻ3�%{�Ѽ��{��y����>��_ ��w1���E�@�ec�H;�>{�9�
4�l����hh�TJ�*�O�I���pZ�kh�юmCE'�6J����^4�L;}�1J%�I����S6s'������Z�)��Xr��]>�<�:���Nv���j���}�tc���б]�%�,��{�3�h�hh�����q�1��V��vy���(�)���N��H�4�QgJ�m�^$4���8����pMǸX`j͖ݢ#���_ga�ߧ��8��WG��VT��<�xT)��:R��j{G�v]�k���QW2�y�z'cF8�!��x?���͵:t6$/+ъ�%zG�^hm`�G�V<�1�t| �GH�R����6��q' �f���0�8XN%�/�b$�I��A��J�5��ܙ�e��;������hL�8���^��΢����mT���q�Z&v��>^�
9Ӊ!�p3��rqN�4^�cYK;'0�x��A���ҋ:�p��v�Վmd`-�V���#�(�z�
�	��)}\����e�h��%ɶ3��mr�pl�k�9Q,�Z�y�Z�og�pw�{^��e,�|$��� �AMYx��-�7
U���:o*��xE`�D��`�7a�Dμ��sC1�(���n�L+�pE�U�����Š�\N��	�ī���x� �ݗ��9�i+on�!�s�#��� �V�1����Y`[jX7�7Q�ܜ$s�D1'??��Te���M�x�8ofǒk��#0-K��8J*�*q��Z���~0�H�]���yr�K�Ԋ��#&~��X.U�8��aC�j�v����!��DP��`Y�����'��rU\����t�Q�*���(��(�mG�o݀yIq���Um+�Ys�,�>�]��)�|�I���M'��;��>o�������77����FY�1
=��=[�_��Z�d{Xl�����t��̛�,IɮzC22��G#��tqy���k\��)�y!��?�5o����h�3��P�, FhA��k�;�{����b;(�oa;�]�o�g�.�L����u<G!)�7M��0ug"����]<%��vG}����b&Ӕ�b�Mݑ�8����VV�6#��s�b^@/W���~���y��c��i$aa Wx��A�J�ϫ�Q��xM�:�/�K�p��|�2?|_��5|�x|��Χ��MJߢ| �U����V{5��yK�0��p�D����<���$�ѷ�������p��n��g��"R��q��:+smW�F�`��\��~�!y��*�w�l�~�!K��&#c^92	�zDJ>����D,?{�M�?��ѺUxle��ۘ�������sG�{�`�2�g(�a����E��I����?"�!�Q�C�s��B���������2�5��u��?@\CV���Ҕy�>�oR��$�	���L�ƅY�5��X7j�ƔB��M�H����:Ɏ7hN�-r�C�->#|���|�{�T�L%I�K���XY�w�F�.��,)�"�#�.��/)�	��À�CY��Z��~�@G�����ƣk��)��b����,OɾbЗCrx2�w�{.^�(2H�݁���M55d�q��%�0Oy��^����S;Q�R�5C�G/ۂo~�������0��kT�Zî�x�7�������ӳ~mui�9UB�}{]YR�\WV���_�p�_�7�E�/���$kRj`]����������U�'�Y��٣�P�3!jw(���]Q�~�?PK   }y�X�v�E�  a  :   org/apache/maven/wrapper/cli/ParsedCommandLineOption.class�TKOQ=��-��Uax�m��(�G������IM�.��N������čLT�$��e���@���E�wN���=������y<������ ��[|��7+���V��tS�s�D�(!����l�P�,������ݐ%�ȸ�^���UQy����Z�ۚ���x��Yj���W�jj��z���r^7�����M��FKk��|[Hhd#��'#�~�P�7�"�1�!a�a��~�0�
7
6����%���2�:J��5�n+U��psGqt��h��`"ߦ,ؖnV��b7FpM¨�1(�κd4�]��jÞ"9Vj-����5ӂkJ�uLSBE���j2�9���ę�fDvJ�nR6/�;{�������Izs�ְw��br�e�٩�^/�/<��Җ�e���z٪mj�k�i��K�(rK���:�1���E �5ě�@"y�C���ox��������a�xA����%�Ҫi&%�O�����-��=Ӆ��*J�D�������&���|���c���6!9��DS`�#H�E�}���g��<�d7w�q���ǵ�qeR��C��c�\�����!�iV<x��b҇_��O�~G1N�@[/AA&0�I$hy��O�����	}�,9>TzQA��RH�&O�=�5�=W�SBD� �9D҇7�M��&�����S����{�bn7f� >"��tR��q��ةR��Eɢcd�/PK   }y�Xx��͊  5  H   org/apache/maven/wrapper/cli/ProjectPropertiesCommandLineConverter.class���N1��
"��n\�&0w^Ÿ"J�q_��P23����te���-�18Q�8�����M_ߞ_ b'���H0�CM*��1�:�|@�{�x�r�@�g��V��%���j2yԮ��j2�C�XDB�2�J��r��C��+�%OWL�s(0�7I;�f[w��I��9���N�S"�N��~թ4¡�����T�f�h���xc|�o}�k�MKV��-R7��X�n�v=Ӻ#z��}İ�Ӈixk!#��R����<�ݦV"�u����4�v���7��oʾ��R������ {X0?Ŏ4��+&.��k23y��	��Lf�lbf$na���؀"VG��'pe��ه)t���D7йit/�L@�����kX��;PK   }y�Xkn�4�  &  G   org/apache/maven/wrapper/cli/SystemPropertiesCommandLineConverter.class��AO�0��nK��Bi�1��2���\&q &�V;�2&e��M��$��(�j��v�؇�p�2M%�I������=�m����� |�Q+(հ�2C_*��1���|N�s�x�r�@8�D+��JIJ�2y4���2��C�LDBb(�;#X�2l&i�)�Ʀ��ÉK�^�v������S�/GWv�z~���4�=�9f��U��p�Yw��%����C=����1�o|�9�F\�,_�%=�y��O>eh�����u����ڝ�w>�N�#�q��oJ��HsЄ�8g/�Xu��r�<�,2��������-�5�Q�>��u�9fff��pvg�X^���0��,@�|�+�����ʏ5����}���YG[��A�����A��FVu� PK   }y�X��c  H
  =   META-INF/maven/org.apache.maven.wrapper/maven-wrapper/pom.xml�VMs�6��WluJfL�9t\�%��jl�c�qs����X 4��HJ��8�N}����ݷowIE�e/�4��jt�� E*3.������u�1��_|߻�)
�	f�0�XJ��4S��3ލ���@GT zRA)	�Ja_ԆEX�KF 	��=��'�o`��2�['J�p�" ��H�K�ò�ۤ� .�P:
�)[%�6��+�T�W�in+Hn{���R}Yw��v���V/8N��Qw5z��s-��4Pk��\�XbI�ʪ�L��um��o]�0���U r9�3��2��æi�hR�a_UxG2N���zޣ(PkP�w���� ��H�D�`X=m?\�)w�H[��x���;�zVT�@1��	L�|'���&��g�sx?<����M��<�^O�ٔN�0�~�L��'��%�u�,w"ȭv����n���CW��%O�$��,G�%͸��P�*�����-���ܸ��ޫz��i�+%�cj�vE�Q'y�^PE�sv~N�9��Z�-�6��p���ӳ����K�s�����R;�L���M�>Y�5��y��D�̰�8vwQ�g��U�nab7eQ�d]M����)�|��B�=��3e����-��Q�p h=��P|\�Q�-����y?�E
Vb|o1��b�F8g��u�xe�~����d�	�E70�T��vW���@b;��	�4wc�w?A��v�A�x-�
iE�Qw�lM��pД+S�sЈ}�v�C�8?��]jzal�o��e�����>+n^M�>Y��;��*~���F���37�MJ�O�e�����3�U�Q����[G%�[��̳��K_a�L?�㵨y�=Y��i~w��mh:�mLk,��o��T�\q����1�:�iѲV鶐m�aE�Q��y���V�g^�� �V�=�1'�̙(6csӓ}����?�=��t�_��rM�ݔ~�l� �tGn^�Ya�@���~���N��-,
C��Y���c��PK   }y�X�\�@   H   D   META-INF/maven/org.apache.maven.wrapper/maven-wrapper/pom.propertiesK,*�LKL.�L��M,K��-/J,(H-�J/�/- ����%$&g������g�����q PK
    }y�X            	          �A    META-INF/PK   }y�X�1Oe�   J             ��'   META-INF/MANIFEST.MFPK
    }y�X                      �A�   org/PK
    }y�X                      �A!  org/apache/PK
    }y�X                      �AJ  org/apache/maven/PK
    }y�X                      �Ay  org/apache/maven/wrapper/PK
    }y�X                      �A�  org/apache/maven/wrapper/cli/PK
    }y�X                      �A�  META-INF/maven/PK
    }y�X            (          �A  META-INF/maven/org.apache.maven.wrapper/PK
    }y�X            6          �A^  META-INF/maven/org.apache.maven.wrapper/maven-wrapper/PK   }y�X�G�q                ���  META-INF/DEPENDENCIESPK   }y�X���m  ^,             ��V  META-INF/LICENSEPK   }y�X��w��   �              ���  META-INF/NOTICEPK   }y�X�۱A�  U  3           ���  org/apache/maven/wrapper/BootstrapMainStarter.classPK   }y�X܇�H  C  2           ���  org/apache/maven/wrapper/DefaultDownloader$1.classPK   }y�X�4'0  �  S           ��s  org/apache/maven/wrapper/DefaultDownloader$SystemPropertiesProxyAuthenticator.classPK   }y�X�3�    0           ��   org/apache/maven/wrapper/DefaultDownloader.classPK   }y�X�y]�   �   )           �� .  org/apache/maven/wrapper/Downloader.classPK   }y�XK>8ڤ  {
  4           ��/  org/apache/maven/wrapper/HashAlgorithmVerifier.classPK   }y�XXW1�  *  *           ��5  org/apache/maven/wrapper/Installer$1.classPK   }y�X[/A�  �#  (           ��r8  org/apache/maven/wrapper/Installer.classPK   }y�X;n4GR  %  %           ���I  org/apache/maven/wrapper/Logger.classPK   }y�Xb`3�N  ,  /           ��L  org/apache/maven/wrapper/MavenWrapperMain.classPK   }y�X���|�    >           ���X  org/apache/maven/wrapper/PathAssembler$LocalDistribution.classPK   }y�X\�@j#  �  ,           ���Z  org/apache/maven/wrapper/PathAssembler.classPK   }y�XR(��  c  6           ��#a  org/apache/maven/wrapper/SystemPropertiesHandler.classPK   }y�X��   a  '           ���g  org/apache/maven/wrapper/Verifier.classPK   }y�X�W!  �
  3           ���h  org/apache/maven/wrapper/WrapperConfiguration.classPK   }y�X��e��    .           ��5m  org/apache/maven/wrapper/WrapperExecutor.classPK   }y�X��   T  ?           ��Rz  org/apache/maven/wrapper/cli/AbstractCommandLineConverter.classPK   }y�Xm�v��  -  I           ���}  org/apache/maven/wrapper/cli/AbstractPropertiesCommandLineConverter.classPK   }y�X�:�dP  g  ?           ���  org/apache/maven/wrapper/cli/CommandLineArgumentException.classPK   }y�Xlk�I  �  7           ����  org/apache/maven/wrapper/cli/CommandLineConverter.classPK   }y�X��I�U  �  4           ��=�  org/apache/maven/wrapper/cli/CommandLineOption.classPK   }y�X�#�ر     6           ���  org/apache/maven/wrapper/cli/CommandLineParser$1.classPK   }y�X�@Ƀ  �  I           ���  org/apache/maven/wrapper/cli/CommandLineParser$AfterFirstSubCommand.classPK   }y�X�wM��  0  A           ��Ӑ  org/apache/maven/wrapper/cli/CommandLineParser$AfterOptions.classPK   }y�X1�GX6  �  J           ���  org/apache/maven/wrapper/cli/CommandLineParser$BeforeFirstSubCommand.classPK   }y�X�w5fr  e  T           ����  org/apache/maven/wrapper/cli/CommandLineParser$CaseInsensitiveStringComparator.classPK   }y�X���  o  K           ��s�  org/apache/maven/wrapper/cli/CommandLineParser$KnownOptionParserState.classPK   }y�XmKs�  �  J           ��̣  org/apache/maven/wrapper/cli/CommandLineParser$MissingOptionArgState.classPK   }y�XV�{�  �  K           ���  org/apache/maven/wrapper/cli/CommandLineParser$OptionAwareParserState.classPK   }y�X��`�  �  E           ��=�  org/apache/maven/wrapper/cli/CommandLineParser$OptionComparator.classPK   }y�X7A�ϳ  !  F           ����  org/apache/maven/wrapper/cli/CommandLineParser$OptionParserState.classPK   }y�XO��4�    A           ����  org/apache/maven/wrapper/cli/CommandLineParser$OptionString.classPK   }y�XX����  �  K           ����  org/apache/maven/wrapper/cli/CommandLineParser$OptionStringComparator.classPK   }y�X����	  2  @           ���  org/apache/maven/wrapper/cli/CommandLineParser$ParserState.classPK   }y�X�Ć��  g  M           ��O�  org/apache/maven/wrapper/cli/CommandLineParser$UnknownOptionParserState.classPK   }y�X/���x  �)  4           ����  org/apache/maven/wrapper/cli/CommandLineParser.classPK   }y�X��sP  �  4           ��{�  org/apache/maven/wrapper/cli/ParsedCommandLine.classPK   }y�X�v�E�  a  :           ���  org/apache/maven/wrapper/cli/ParsedCommandLineOption.classPK   }y�Xx��͊  5  H           ��c�  org/apache/maven/wrapper/cli/ProjectPropertiesCommandLineConverter.classPK   }y�Xkn�4�  &  G           ��S�  org/apache/maven/wrapper/cli/SystemPropertiesCommandLineConverter.classPK   }y�X��c  H
  =           ��F�  META-INF/maven/org.apache.maven.wrapper/maven-wrapper/pom.xmlPK   }y�X�\�@   H   D           ����  META-INF/maven/org.apache.maven.wrapper/maven-wrapper/pom.propertiesPK    7 7 �  O�    
```

## intervention-service\.mvn\wrapper\maven-wrapper.properties

```bash
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip
wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar

```

## intervention-service\src\main\java\tg\ngstars\interv\InterventionServiceApplication.java

```java
package tg.ngstars.interv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InterventionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterventionServiceApplication.class, args);
    }
}

```

## intervention-service\src\main\java\tg\ngstars\interv\client\MediaClient.java

```java
package tg.ngstars.interv.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class MediaClient {

    private static final Logger log = LoggerFactory.getLogger(MediaClient.class);

    private final RestClient restClient;
    private final String mediaBaseUrl;

    public MediaClient(@Qualifier("mediaRestClient") RestClient restClient,
            String mediaBaseUrl) {
        this.restClient = restClient;
        this.mediaBaseUrl = mediaBaseUrl;
    }

    public record UploadResponse(String filename) {}

    public String uploadFile(MultipartFile file) {
        var response = restClient.post()
                .uri("/upload")
                .body(createMultipartBody(file))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    var error = new String(res.getBody().readAllBytes());
                    log.error("Media service error on upload: status={}, body={}", res.getStatusCode(), error);
                    throw new tg.ngstars.interv.exception.MediaServiceException("Upload echoue: " + res.getStatusCode());
                })
                .body(UploadResponse.class);

        if (response == null || response.filename() == null)
            throw new tg.ngstars.interv.exception.MediaServiceException("Reponse media-service invalide : filename absent");

        return UriComponentsBuilder.fromUriString(mediaBaseUrl)
                .pathSegment("api", "media", response.filename())
                .build().toUriString();
    }

    public String uploadBase64(String base64Data) {
        String data = base64Data.replaceAll("^data:image/[^;]+;base64,", "");
        var response = restClient.post()
                .uri("/upload-base64")
                .body(Map.of("data", data))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    var error = new String(res.getBody().readAllBytes());
                    log.error("Media service error on base64 upload: status={}, body={}", res.getStatusCode(), error);
                    throw new tg.ngstars.interv.exception.MediaServiceException("Upload base64 echoue: " + res.getStatusCode());
                })
                .body(UploadResponse.class);

        if (response == null || response.filename() == null)
            throw new tg.ngstars.interv.exception.MediaServiceException("Reponse media-service invalide : filename absent");

        return UriComponentsBuilder.fromUriString(mediaBaseUrl)
                .pathSegment("api", "media", response.filename())
                .build().toUriString();
    }

    public void deleteFile(String filename) {
        restClient.delete()
                .uri("/{filename}", filename)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    var error = new String(res.getBody().readAllBytes());
                    log.error("Media service error on delete: status={}, body={}", res.getStatusCode(), error);
                    throw new tg.ngstars.interv.exception.MediaServiceException("Suppression echouee: " + res.getStatusCode());
                })
                .toBodilessEntity();
    }

    private org.springframework.http.HttpEntity<?> createMultipartBody(MultipartFile file) {
        var headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);
        var body = new org.springframework.util.LinkedMultiValueMap<String, Object>();
        body.add("file", file.getResource());
        return new org.springframework.http.HttpEntity<>(body, headers);
    }
}

```

## intervention-service\src\main\java\tg\ngstars\interv\config\GlobalExceptionHandler.java

```java
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
import tg.ngstars.interv.exception.MediaServiceException;
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

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Conflict");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("about:blank"));
        return problem;
    }

    @ExceptionHandler(MediaServiceException.class)
    public ProblemDetail handleMediaService(MediaServiceException ex) {
        log.error("Media service error", ex);
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        problem.setTitle("Bad Gateway");
        problem.setDetail("Media service unavailable");
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

```

## intervention-service\src\main\java\tg\ngstars\interv\config\MediaClientConfig.java

```java
package tg.ngstars.interv.config;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;

@Configuration
public class MediaClientConfig {

    @Bean
    public RestClient mediaRestClient(
            @Value("${media-service.url:http://localhost:8084}") String baseUrl) {
        var httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        var factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(10));
        return RestClient.builder()
                .baseUrl(baseUrl + "/api/media")
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
}

```

## intervention-service\src\main\java\tg\ngstars\interv\config\SecurityConfig.java

```java
package tg.ngstars.interv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import tg.ngstars.common.security.RealmRoleConverter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
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
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new RealmRoleConverter());
        return converter;
    }
}

```

## intervention-service\src\main\java\tg\ngstars\interv\controller\InterventionController.java

```java
package tg.ngstars.interv.controller;

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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

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
    public ResponseEntity<StreamingResponseBody> generatePdf(@PathVariable UUID id) {
        var userId = securityUtils.getCurrentUserId();
        StreamingResponseBody stream = out -> interventionService.generatePdfToStream(id, userId, securityUtils.isAdminOrManager(), out);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=intervention.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(stream);
    }

    @GetMapping("/by-client/{clientId}")
    public ResponseEntity<Page<InterventionResponse>> getClientInterventions(
            @PathVariable UUID clientId, Pageable pageable) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.getClientInterventions(clientId, userId, securityUtils.isAdminOrManager(), pageable));
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

```

## intervention-service\src\main\java\tg\ngstars\interv\controller\PhotoController.java

```java
package tg.ngstars.interv.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import tg.ngstars.interv.dto.PhotoResponse;
import tg.ngstars.interv.service.PhotoService;

@RestController
@RequestMapping("/api/interventions/{id}/photos")
public class PhotoController {

    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "image/jpeg", "image/png", "image/webp");

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TECHNICIAN','MANAGER','ADMIN')")
    public ResponseEntity<PhotoResponse> upload(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude) {

        if (!ALLOWED_MIME_TYPES.contains(file.getContentType()))
            throw new IllegalArgumentException("Type de fichier non autorise: " + file.getContentType());

        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(photoService.addPhoto(id, file, type, latitude, longitude));
        } catch (java.io.IOException e) {
            throw new tg.ngstars.interv.exception.MediaServiceException("Erreur upload photo: " + e.getMessage(), e);
        }
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

```

## intervention-service\src\main\java\tg\ngstars\interv\controller\SignatureController.java

```java
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

```

## intervention-service\src\main\java\tg\ngstars\interv\controller\SyncController.java

```java
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

```

## intervention-service\src\main\java\tg\ngstars\interv\dto\CreateInterventionRequest.java

```java
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

```

## intervention-service\src\main\java\tg\ngstars\interv\dto\InterventionResponse.java

```java
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

```

## intervention-service\src\main\java\tg\ngstars\interv\dto\ItemRequest.java

```java
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

```

## intervention-service\src\main\java\tg\ngstars\interv\dto\ItemResponse.java

```java
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

```

## intervention-service\src\main\java\tg\ngstars\interv\dto\PhotoResponse.java

```java
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

```

## intervention-service\src\main\java\tg\ngstars\interv\dto\SignatureRequest.java

```java
package tg.ngstars.interv.dto;

import jakarta.validation.constraints.NotBlank;

public record SignatureRequest(
    @NotBlank String imageBase64
) {}

```

## intervention-service\src\main\java\tg\ngstars\interv\dto\SyncRequest.java

```java
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

```

## intervention-service\src\main\java\tg\ngstars\interv\dto\UpdateBillingRequest.java

```java
package tg.ngstars.interv.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateBillingRequest(
    boolean billable,
    @Positive BigDecimal billingAmount,
    @Size(max = 1000) String billingNotes
) {}

```

## intervention-service\src\main\java\tg\ngstars\interv\dto\UpdateDiagnosisRequest.java

```java
package tg.ngstars.interv.dto;

import jakarta.validation.constraints.Size;

public record UpdateDiagnosisRequest(
    @Size(max = 5000) String diagnosis,
    @Size(max = 5000) String workDone
) {}

```

## intervention-service\src\main\java\tg\ngstars\interv\dto\UpdateEquipmentRequest.java

```java
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

```

## intervention-service\src\main\java\tg\ngstars\interv\dto\UpdateRecommendationsRequest.java

```java
package tg.ngstars.interv.dto;

import jakarta.validation.constraints.Size;

public record UpdateRecommendationsRequest(
    @Size(max = 5000) String recommendations
) {}

```

## intervention-service\src\main\java\tg\ngstars\interv\dto\UpdateResultRequest.java

```java
package tg.ngstars.interv.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateResultRequest(
    @NotBlank String result
) {}

```

## intervention-service\src\main\java\tg\ngstars\interv\dto\UpdateScheduleRequest.java

```java
package tg.ngstars.interv.dto;

import java.time.OffsetDateTime;

public record UpdateScheduleRequest(
    OffsetDateTime departureTime,
    OffsetDateTime arrivalTime,
    OffsetDateTime startTime,
    OffsetDateTime endTime
) {}

```

## intervention-service\src\main\java\tg\ngstars\interv\exception\ForbiddenException.java

```java
package tg.ngstars.interv.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) { super(message); }
}

```

## intervention-service\src\main\java\tg\ngstars\interv\exception\MediaServiceException.java

```java
package tg.ngstars.interv.exception;

public class MediaServiceException extends RuntimeException {
    public MediaServiceException(String message) {
        super(message);
    }

    public MediaServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

```

## intervention-service\src\main\java\tg\ngstars\interv\exception\NotFoundException.java

```java
package tg.ngstars.interv.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}

```

## intervention-service\src\main\java\tg\ngstars\interv\model\Intervention.java

```java
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
@Getter @Setter @ToString(exclude = "items")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Intervention {

    @Id
    @EqualsAndHashCode.Include
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

```

## intervention-service\src\main\java\tg\ngstars\interv\model\InterventionItem.java

```java
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
    @EqualsAndHashCode.Include
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

```

## intervention-service\src\main\java\tg\ngstars\interv\model\InterventionPhoto.java

```java
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

```

## intervention-service\src\main\java\tg\ngstars\interv\model\PhotoType.java

```java
package tg.ngstars.interv.model;

public enum PhotoType {
    BEFORE,
    AFTER
}

```

## intervention-service\src\main\java\tg\ngstars\interv\repository\InterventionPhotoRepository.java

```java
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

```

## intervention-service\src\main\java\tg\ngstars\interv\repository\InterventionRepository.java

```java
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
    Page<Intervention> findByClientIdOrderByCreatedAtDesc(UUID clientId, Pageable pageable);

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

```

## intervention-service\src\main\java\tg\ngstars\interv\service\InterventionService.java

```java
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

    public Page<InterventionResponse> getClientInterventions(UUID clientId, UUID userId, boolean isAdminOrManager, Pageable pageable) {
        return interventionRepository.findByClientIdOrderByCreatedAtDesc(clientId, pageable)
                .map(this::toResponse);
    }

    public byte[] generatePdf(UUID id, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        return PdfService.generate(intervention);
    }

    public void generatePdfToStream(UUID id, UUID userId, boolean isAdminOrManager, java.io.OutputStream out) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        PdfService.write(intervention, out);
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
        checkOwnership(intervention, userId, isAdminOrManager);
        if ("COMPLETED".equals(intervention.getStatus()))
            throw new IllegalStateException("Intervention already completed");
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

```

## intervention-service\src\main\java\tg\ngstars\interv\service\PdfService.java

```java
package tg.ngstars.interv.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import tg.ngstars.interv.model.Intervention;
import tg.ngstars.interv.model.InterventionItem;

public final class PdfService {

    private PdfService() {}

    public static void write(Intervention intervention, OutputStream out) {
        var document = new Document();
        try {
            PdfWriter.getInstance(document, out);
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
    }

    public static byte[] generate(Intervention intervention) {
        var baos = new ByteArrayOutputStream();
        write(intervention, baos);
        return baos.toByteArray();
    }
}

```

## intervention-service\src\main\java\tg\ngstars\interv\service\PhotoService.java

```java
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

```

## intervention-service\src\main\java\tg\ngstars\interv\service\SecurityUtils.java

```java
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

```

## intervention-service\src\main\java\tg\ngstars\interv\service\SignatureService.java

```java
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

```

## intervention-service\src\main\resources\application-dev.yml

```yaml
spring:
  jpa:
    show-sql: true

logging:
  level:
    tg.ngstars: DEBUG

```

## intervention-service\src\main\resources\application-prod.yml

```yaml
springdoc:
  swagger-ui:
    enabled: false
  api-docs:
    enabled: false

logging:
  level:
    tg.ngstars: WARN

```

## intervention-service\src\main\resources\application.yml

```yaml
server:
  port: 8083
  shutdown: graceful
  forward-headers-strategy: native

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
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
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
    enabled: false
    path: /swagger-ui.html

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    env:
      keys-to-sanitize: password,secret,token,credential

```

## intervention-service\src\main\resources\db\migration\V1__init_schema.sql

```sql
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

```

## intervention-service\src\main\resources\db\migration\V2__add_photos_and_signatures.sql

```sql
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

```

## intervention-service\src\main\resources\db\migration\V3__add_intervention_sections.sql

```sql
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

```

## intervention-service\src\main\resources\db\migration\V4__add_client_id_index.sql

```sql
CREATE INDEX IF NOT EXISTS idx_interventions_client_id ON interventions(client_id);

```

## intervention-service\src\test\java\tg\ngstars\interv\service\InterventionServiceTest.java

```java
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

```

---

########### media-service ###########

## media-service\.gitignore

```text
target/
*.class
*.jar
*.war
*.log
*.iml
.idea/
*.swp
*.swo
*~
uploads/
application-*.yml
!application.yml
!application-dev.yml
!application-prod.yml
.DS_Store

```

## media-service\mvnw.cmd

```text
<# : batch portion
@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.4
@REM
@REM Optional ENV vars
@REM   MVNW_REPOURL - repo url base for downloading maven distribution
@REM   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
@REM   MVNW_VERBOSE - true: enable verbose log; others: silence the output
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
@SET PSModulePath=
@FOR /F "usebackq tokens=1* delims==" %%A IN (`powershell -noprofile "& {$scriptDir='%~dp0'; $script='%__MVNW_ARG0_NAME__%'; icm -ScriptBlock ([Scriptblock]::Create((Get-Content -Raw '%~f0'))) -NoNewScope}"`) DO @(
  IF "%%A"=="MVN_CMD" (set __MVNW_CMD__=%%B) ELSE IF "%%B"=="" (echo %%A) ELSE (echo %%A=%%B)
)
@SET PSModulePath=%__MVNW_PSMODULEP_SAVE%
@SET __MVNW_PSMODULEP_SAVE=
@SET __MVNW_ARG0_NAME__=
@SET MVNW_USERNAME=
@SET MVNW_PASSWORD=
@IF NOT "%__MVNW_CMD__%"=="" ("%__MVNW_CMD__%" %*)
@echo Cannot start maven from wrapper >&2 && exit /b 1
@GOTO :EOF
: end batch / begin powershell #>

$ErrorActionPreference = "Stop"
if ($env:MVNW_VERBOSE -eq "true") {
  $VerbosePreference = "Continue"
}

# calculate distributionUrl, requires .mvn/wrapper/maven-wrapper.properties
$distributionUrl = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionUrl
if (!$distributionUrl) {
  Write-Error "cannot read distributionUrl property in $scriptDir/.mvn/wrapper/maven-wrapper.properties"
}

switch -wildcard -casesensitive ( $($distributionUrl -replace '^.*/','') ) {
  "maven-mvnd-*" {
    $USE_MVND = $true
    $distributionUrl = $distributionUrl -replace '-bin\.[^.]*$',"-windows-amd64.zip"
    $MVN_CMD = "mvnd.cmd"
    break
  }
  default {
    $USE_MVND = $false
    $MVN_CMD = $script -replace '^mvnw','mvn'
    break
  }
}

# apply MVNW_REPOURL and calculate MAVEN_HOME
# maven home pattern: ~/.m2/wrapper/dists/{apache-maven-<version>,maven-mvnd-<version>-<platform>}/<hash>
if ($env:MVNW_REPOURL) {
  $MVNW_REPO_PATTERN = if ($USE_MVND -eq $False) { "/org/apache/maven/" } else { "/maven/mvnd/" }
  $distributionUrl = "$env:MVNW_REPOURL$MVNW_REPO_PATTERN$($distributionUrl -replace "^.*$MVNW_REPO_PATTERN",'')"
}
$distributionUrlName = $distributionUrl -replace '^.*/',''
$distributionUrlNameMain = $distributionUrlName -replace '\.[^.]*$','' -replace '-bin$',''

$MAVEN_M2_PATH = "$HOME/.m2"
if ($env:MAVEN_USER_HOME) {
  $MAVEN_M2_PATH = "$env:MAVEN_USER_HOME"
}

if (-not (Test-Path -Path $MAVEN_M2_PATH)) {
    New-Item -Path $MAVEN_M2_PATH -ItemType Directory | Out-Null
}

$MAVEN_WRAPPER_DISTS = $null
if ((Get-Item $MAVEN_M2_PATH).Target[0] -eq $null) {
  $MAVEN_WRAPPER_DISTS = "$MAVEN_M2_PATH/wrapper/dists"
} else {
  $MAVEN_WRAPPER_DISTS = (Get-Item $MAVEN_M2_PATH).Target[0] + "/wrapper/dists"
}

$MAVEN_HOME_PARENT = "$MAVEN_WRAPPER_DISTS/$distributionUrlNameMain"
$MAVEN_HOME_NAME = ([System.Security.Cryptography.SHA256]::Create().ComputeHash([byte[]][char[]]$distributionUrl) | ForEach-Object {$_.ToString("x2")}) -join ''
$MAVEN_HOME = "$MAVEN_HOME_PARENT/$MAVEN_HOME_NAME"

if (Test-Path -Path "$MAVEN_HOME" -PathType Container) {
  Write-Verbose "found existing MAVEN_HOME at $MAVEN_HOME"
  Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"
  exit $?
}

if (! $distributionUrlNameMain -or ($distributionUrlName -eq $distributionUrlNameMain)) {
  Write-Error "distributionUrl is not valid, must end with *-bin.zip, but found $distributionUrl"
}

# prepare tmp dir
$TMP_DOWNLOAD_DIR_HOLDER = New-TemporaryFile
$TMP_DOWNLOAD_DIR = New-Item -Itemtype Directory -Path "$TMP_DOWNLOAD_DIR_HOLDER.dir"
$TMP_DOWNLOAD_DIR_HOLDER.Delete() | Out-Null
trap {
  if ($TMP_DOWNLOAD_DIR.Exists) {
    try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
    catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
  }
}

New-Item -Itemtype Directory -Path "$MAVEN_HOME_PARENT" -Force | Out-Null

# Download and Install Apache Maven
Write-Verbose "Couldn't find MAVEN_HOME, downloading and installing it ..."
Write-Verbose "Downloading from: $distributionUrl"
Write-Verbose "Downloading to: $TMP_DOWNLOAD_DIR/$distributionUrlName"

$webclient = New-Object System.Net.WebClient
if ($env:MVNW_USERNAME -and $env:MVNW_PASSWORD) {
  $webclient.Credentials = New-Object System.Net.NetworkCredential($env:MVNW_USERNAME, $env:MVNW_PASSWORD)
}
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
$webclient.DownloadFile($distributionUrl, "$TMP_DOWNLOAD_DIR/$distributionUrlName") | Out-Null

# If specified, validate the SHA-256 sum of the Maven distribution zip file
$distributionSha256Sum = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionSha256Sum
if ($distributionSha256Sum) {
  if ($USE_MVND) {
    Write-Error "Checksum validation is not supported for maven-mvnd. `nPlease disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties."
  }
  Import-Module $PSHOME\Modules\Microsoft.PowerShell.Utility -Function Get-FileHash
  if ((Get-FileHash "$TMP_DOWNLOAD_DIR/$distributionUrlName" -Algorithm SHA256).Hash.ToLower() -ne $distributionSha256Sum) {
    Write-Error "Error: Failed to validate Maven distribution SHA-256, your Maven distribution might be compromised. If you updated your Maven version, you need to update the specified distributionSha256Sum property."
  }
}

# unzip and move
Expand-Archive "$TMP_DOWNLOAD_DIR/$distributionUrlName" -DestinationPath "$TMP_DOWNLOAD_DIR" | Out-Null

# Find the actual extracted directory name (handles snapshots where filename != directory name)
$actualDistributionDir = ""

# First try the expected directory name (for regular distributions)
$expectedPath = Join-Path "$TMP_DOWNLOAD_DIR" "$distributionUrlNameMain"
$expectedMvnPath = Join-Path "$expectedPath" "bin/$MVN_CMD"
if ((Test-Path -Path $expectedPath -PathType Container) -and (Test-Path -Path $expectedMvnPath -PathType Leaf)) {
  $actualDistributionDir = $distributionUrlNameMain
}

# If not found, search for any directory with the Maven executable (for snapshots)
if (!$actualDistributionDir) {
  Get-ChildItem -Path "$TMP_DOWNLOAD_DIR" -Directory | ForEach-Object {
    $testPath = Join-Path $_.FullName "bin/$MVN_CMD"
    if (Test-Path -Path $testPath -PathType Leaf) {
      $actualDistributionDir = $_.Name
    }
  }
}

if (!$actualDistributionDir) {
  Write-Error "Could not find Maven distribution directory in extracted archive"
}

Write-Verbose "Found extracted Maven distribution directory: $actualDistributionDir"
Rename-Item -Path "$TMP_DOWNLOAD_DIR/$actualDistributionDir" -NewName $MAVEN_HOME_NAME | Out-Null
try {
  Move-Item -Path "$TMP_DOWNLOAD_DIR/$MAVEN_HOME_NAME" -Destination $MAVEN_HOME_PARENT | Out-Null
} catch {
  if (! (Test-Path -Path "$MAVEN_HOME" -PathType Container)) {
    Write-Error "fail to move MAVEN_HOME"
  }
} finally {
  try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
  catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
}

Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"

```

## media-service\pom.xml

```xml
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
            <groupId>tg.ngstars</groupId>
            <artifactId>ng-fields-shared-lib</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
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

```

## media-service\.mvn\wrapper\maven-wrapper.jar

```text
PK
    }y�X            	   META-INF/PK   }y�X�1Oe�   J     META-INF/MANIFEST.MF���
�0����]�N� A����Fss�}aA]���|�0��>�=�^Y�!f�<����7����"��VGe eˉ��%-�VIL���V5"�_�VA����s�~ν�)K?7#��P�2'�1*ۆ��k;��H���M�����n�|��ӓ�PK
    }y�X               org/PK
    }y�X               org/apache/PK
    }y�X               org/apache/maven/PK
    }y�X               org/apache/maven/wrapper/PK
    }y�X               org/apache/maven/wrapper/cli/PK
    }y�X               META-INF/maven/PK
    }y�X            (   META-INF/maven/org.apache.maven.wrapper/PK
    }y�X            6   META-INF/maven/org.apache.maven.wrapper/maven-wrapper/PK   }y�X�G�q        META-INF/DEPENDENCIES��A
1E�=E.����N�u���4	i�����v��!?g:�Lʙn�ڤ�*ph�΂F�PJ#[1�!;����V��~^y����ŃU���_߁�����ʦ��k
vGЅ#| PK   }y�X���m  ^,     META-INF/LICENSE�Z[s��~���r�Si��4i牱䆭CiD�n&��%� ���. ���=����dw�VM�5I�ٳ�����E/˝�u�:�^<��?�u�t����B�Mv�������<�h7��W����\�6sc���ʽz���~Z���J��Y]-�7��xws'>��qw}{ws��-~]�SW������	�j.�T�;=�rn��k3�'�	��M#Z%;1�Ie['dW��t���bt�V��Tc�_^>[i7X��{!��pKU��A�U�B��֌۝�N�>hxΔc���X/cO+M�z���w�
P	�� �8�������s+��l��v[z��!S@me#�I�c�$핐%I	Z��Y/��^A�o�i
!�
R����cW��Ҵ��$����a�rxùxg,�я�71ɪ���G3/eFGq�B_�R�W� �Y�*�;�w!#J	N����,`E+;�U�<�׍��+V��N������$ٹe��	�\hЄ��v�GI������%������/i;�a�A���:� �d�A�Fu`�R�+'�3=��6�L\�Z���]�^���&�Q�y|x����[�<�'��$�ְ[	)��GZoU�����kM��[���p4IY�������3�ht�qw�3����r�!8���#A^?P����v��;��Q|�l��p�����cl(?jkZ�����	Q�9|R���o��R�yH\1=��qtLH�^cBR�s� g��'��N����P�n�*-�p��c4��	(��KҘp#-����1b����ZY�<H��M�?å���>�dą�n`x8�[
�dV9X[�BA[/��e��ΰ�Ⓥ�W��#$Sc���
W����h7;� ����$�AP|#:��T�p�~��*܊܅����r��8k� �iՃ&Wb�i|�66|��y6yaX唃H!�K��4��Lou�����NՓ�/ı���0���H��V�R��T��)h:F��j��'2��㤓��N� D��%�"��Ѩ'J�u�����"�����9S6�/�'\��Q6�	�p�H�d�6�
~J�"K�Q���M�m7n ;<x�A�E��z>h#��Z�L���j�De��}���5��i��e�^��f^��˰H5��� 腍l(���uD>��[_`�FW�Ph���d!����R�+��K:"�7@)AZV�"r7���5wTXBJ���	v?V>f+�k�F/2�DAfm�p�rtT�iǖ���ȏ�x�4��`��YC<�Q\��ь����B�M�(P.��#�PD�a�F"��l��"����4���u<v���R�܀���Ѧb�l�PFEHJ���$t���mK��r��7K?����H�p۷���Y��������f&K��TI�H ����� 9�S��� �	���T{�\�3�K��Ǘ�z�'s��pxY[�4�S"��Ts��ᆡۂ�c=��	�%8���+B����@�߀�\j}�E޷�4?b1��ϔs�v�3�J���\�2��`�r�"����K��Y3�]a;�������6u�<��j ~�Q��1<Q���`&�M�>
�ʾo��48�����U+����lv8�"	ɭq���uNZM�Y[@���(j_����`�)_���DVOˎ�q��-��$o���b���n.�5�?�B�
c::e�[VAn%�L ���T�"��ƹ�d0<FiF�O�</E#�n��Q[.`��|�G���QM`ŝo���29����S1LŦ�(ShF}��F#�/y�Uqu�E�X�.�
���ҰO�
���;�O��u+	َQpPn3��gX�i#l6�Q!���7�"O�f.�O Y�Z!2H
�V)�rm艸��z�셼䓎i[���~ܪ�Z9���!��TR}8�$��2��d{��&Qi죰硎���Aw'�=�l{���([�-C����e��U$Xxs��Sw .�8n���Kձ��] ,V
yS��	
�!��?� ��s�����g�A�U�-T<&��3��p�INK��h�%�V�o��ճ��������q {c��=�rg��ٕA��L9�,�+ZO	>���)��Y�"(I��fb<�2�A�ŗ�5s��g�J�2%�S���/I�
�6}ԔA�d�d�IT�gu�>�I��y=@	]'����M�T��ũ�e�zٔ��g�Te
� �Y �V/���s�0#�P���wa�_�f��M�[�8�"5��P����"�:Lf�lȪ�[�w�̤ս��$
��G�g�~
�U��jlm�DL ���;�1��`���D�*虘��8��0O�[�5Q�*��Ұ�	���+s
���UƑ�F�:a�g|흹2b1�]���hS����Y<<ъ�ӹ�J$�ΦyI��۪I��g�D�1�&c�ةu�|K͎�	�^5�@7:�����a�Rc�K��8�8��l�����]%��;r��m���Ӛy�Ejf�"��V���ׯ̀���՗���vK��R͍P��_ad.�1��)X1�D[��(�>C�#S��� ��7Ī��|�t�{���?�3]B΁)wv#���jL_�5�lqnN��}����:��C��C��6ժ�F�o���;�	�tr)~���4jV�Q��+bӁ�ړ�lȦ�7_Δ �ԟ��J;j��Ҷ��]1	���7��yc��`��H�K���a>�]R�uš�q��?��ˉs/q��?[��r=?,��u0�����7������bu��^����Z��X�~_����h�~��K'ф+U6&MDsRp� M.��"{
�`��������z�\��[��z����?]߽��\��|����B���~u���^�������N�~���Y_s����o@�6�t�@73�N�<gMo5�s:pх�P�%���<mt87��v��Δ:���������E�i3˱��9|&�E���.ϗXyПn =X|�аt�N;���,�!tj�h`_��,�mw1����g�������!BG�mq�-����v�|~0zN�e��M�� �V�r;�����J@z9��
�ֳ�gH( �|���g�x!��ƙ��j�w�X�c��[��F��9F���ygf��O.��Z���5���&�~��l�^�9����R7��j$�z���"x�M������+��q��x�e�a��4]����� o��r����\,J�	h�����"�,)>O��������B˝1<�I�䲝f���jExPGʮT|��Ǡ�w���Ւ4c�6Awa6��Boy���̗�Z�<�/���Ac���c'ĭd4�3��Go�tMv9���!���4�(�KL'ݢ$DO��,�L{&]3>c�s��m�h�J�Ю�
`�ՙѹ�-!Q �ъ)�Gk�m��&CW��*Q�ӹ����F:�-�l��>�ƌ6F]8��WWXWϽG�/no��?ߠiZ �z�/���o��>�%���.(�k�iB����B>��F�:�Z��r
$;��o)D��_g�h2��!�����Nz..�L����@��A��.u�Ԧ:�	@��;��vv7������"��zV p6/��i?'(N�r�@�!c嶋hf�q�Zݨ��
ݐM.��r4�F�a���|��_PM<���½kϤ!���o�9�e�/��U�Bz��G�����>H��g��O��*.������("�#\���<�x��6��1FT�8"u�fC�29م@�C�Ͻr���j}�T�%_�П���3���N�p�K������H��&�������h��ێp@	�,t�o��iI����\��PK   }y�X��w��   �      META-INF/NOTICE}̱
�0��=Oq���:���Ap��|4g(��rm�����_>����(B
WTwayi��A�����0D����ɝ�VQ	z^r@K��sCLD9,�A��*P~6�J3@s�g��frj�Z��/�S���PK   }y�X�۱A�  U  3   org/apache/maven/wrapper/BootstrapMainStarter.class�Wk{�~ǖ���	�pJ��M�eǖJ 4�!B��vj�R��t%��M�]e����Ҧ)�׶h�Wz/��/��~���~)}g�U��:<�#��9��9������G �G1t�SAHE�m�-S�̹�d�^t"#�i8�t��O�E����n�C�=�ѪZ��g���,�Z��ۙ�,˩9��k��w4��m��f����&�c)����iX�Y��gNhNy�?��`����ɜ�ʍV�Z-gi%�V�M@m_�F�+�Q�;�5
��m�픷�+;6ÝH(إb7��+k�M�6��(v�U�T�|V`���l3U�gNw�j�>�#'<u�%\��S�+�vQ��f�������S3~S6�$C8��
U!��ɲ�k%�-ź-�{��]�T�y�c���Ψe:���shWjS+�xP�~�O�h��V���}�CQ�-Zv�TKW����rF����/� ��P��U��}��c���0���
��DM��y]�Ȉ������X�5�Rw�Fn�Lf��yT�a<&u��^�1jZ��xB�O��F����	�b�8�xqk���(�Q};rE˜��9�]0���>� m�P1�����] �9v~ף�ͳ��d1��W�i��g�`��K*�q����S��W�l<�!h����l�M)��@�O�i_Vq_�s3F61�\�.�ك�{6Z�o�&�#)��/��)(y�� ��ɪcXf��SPVa�<�����m�7l��l�}_`��m��p��(*��Q2Cm�H��li���B����[{e]c�=����:E�������o�͂1xh���W�1*��&�
�pI�WU|_������[�j�5�tVJ|Cųx��1]����~Sŷ�1.V�����^|��f-j���|G��x��P+���j��R�e�S� 	v4�� ��2��sĪ��'����.|?P𒊗��\�rVQ�0=CS��,�xn���5��Q�^)%M�IVHv�S֓㲡%�^�<vx*i�Mj��)l�r�d_o�/��#?V����R��Ef-{^#��S�u��O�	����a�6ϋ�����Y���L�'���>)��x��Ok�!�Mb�),%��\B���|�w��fx��i�w�rw8�Ãi��$wY��M�p*&|u���7�晶}C+t/��ڮ�;#��٥�[�z�#�Hͭ,e=ો�JK�_�%�{��
<��i$Pi��[��Tn�^�0�U]����"��d�_��U����Z((o�R�׺(/��B^���	g��cx`�:_:�S>#.���6�.~�Q����(,�ɧ�vy`p��l���mӝCk�.��;�{{Vp_6���Dh�D(��2�g��k��k8$���3����5�w�OO��_���tg�T~g��DD��1ݹ��3�]�����Ѱ���x���^^���}&�/3��\��s��:z��L�Q��K�v���_��t:����:��)��E�Z��w�=��m��H�?5ak���k�"�:���	�EF%�{����`�MW�_�Ӄ���� ���Dh��p�|{F��g�C�D9.3���9�kL��=��;���'B��2�&B�����{�W=W��
���V��A^z���s�!�4m���=Ȓk{0BT&�F9���+�;�7Z�<{x���Zvs�����-��������E",��ɱ!�Ғ\���!�	��(�*��?~B-��7:r
�|���ׯ@�%C+���n���/PK   }y�X܇�H  C  2   org/apache/maven/wrapper/DefaultDownloader$1.class�TmOA~
�ak�"��R����O5��Zb[�m��.��.�^!|�'��h��Gg�HP �Knvfn�yfgg����?<�3CY��.��͎p�|OHw_��]�ݲ���}9�F,�:�0ư|.� vx?j_���N�1����^%\����������+E���aG��o�Pi��ܗ~��0���-$f�C����W�u�+9�$�d�^i�[+nV�墍똲�r0��(C�eۭ�B�%����cXȔN|�/۹�i�n�%��k���͂�۸cᮃY��Ka&B�u��k�b��g#�����gPoy�:���Lk"橖`��|)*���o�v@�dI5yP��7��3f��a�,��,jU��]꟦3�ȳυa>s������5��M��7��N�����l�G=�`�Fq���;xb�n�����k��Ά�B{�)z��+b��b���$��;�4�H[��x�ť/^<���(f��(�xH25�BY �L6���
�� �%,�ˤ}E�O�@�]��7\Kΐ��[����!Ia�GLL�H��El� |�6�Gb.�1��1�,L,���PK   }y�X�4'0  �  S   org/apache/maven/wrapper/DefaultDownloader$SystemPropertiesProxyAuthenticator.class�SkkA=7M�q�>\5��Z�Z�X��VQ"BI�TՂ��4��d7�N��W���?J��Y��pv��9���wf~����2��H`�B2�a��UwE[��m�]�{J��R����4�j��7�#���A�ek]��=r�����k�&t�#/<��/	C�����ѺD�|����v��-�����E�j�������ŶQ|Jec�.���
a2�h
���LF�R�V��)�V�_/�ϧR�FpN��J!K��2�7��^H��-ܶp'�i̜uع*�V��<��oUl֞5;6G��Qު�7Z	v$a����M��-�;���Sj��)�g�q2�^Hx[��'�L�2�ﻓ��\ܒ�o73�r��*���'������F�Q5��3ɜ��WB���KUi�_ZxD(\\�0��YxBX�<f�"�`�!��2�0�3�G>�.���A��;%�b��� ���1�<
q�g�Z�;�N��q窓���!n���c:�ˀC�=���p|B��QO�1�s�/��+�ľL���QY1»X�����/�r��oPK   }y�X�3�    0   org/apache/maven/wrapper/DefaultDownloader.class�Y	`י��-k�bB@lAJ����C.;68��W-cc �A�yF� w��&i�6=��^��3i�*��p�݋�v�{�g��}d�nw�f�73�%[N	�y�~���+ v�c
�Z-{&���Ԭ���Ofⴭ�r����Io0�g�hPԠ@XsB?�'��9�9~�H9�{3fƹ_���u�!�R�i���V�ט�Yg�u��Zzڰn�J6�ҝ�e�s�@xp�]ұ3�L�d�F�ZI=\q`°�|��ѰN�zS�2�33���3�g�0�ܸAC���T�X�GET`�����9I�@ްC؄��E�f�\e��|�1�V�RIg^`G�rZ�O)�E`��[k����1�KU�(i[�۶	�l��;{Z'B؁[�hhE��Wb�pU<By��)l�]��Ut�041<y�@�l�w�_�2�!!��󔊝�]�������^�t\��!t������ќb*�[���"c%�3Y#1�1O��<�t���W�} �#���O��g2y�/]%s�6���l�������Z���UЧa����4J��6�K������A����C�/8�����Ti���X���1�flF�eg�6coJ���ԔNŠ@���|�Q0�����906�i���d.X�R#lB�&q�:����,�X_�^Ux�@�{c�˘y#�<cF0��N�9V,�#$FH��K�Ů����f=��g��X�%�o||4�QpT�1�M���8h���<8����K�cB��RHSݒ\�cئ���+"�&�e~Id�f@458ց1�cMI��A��N*�j��Y�n_��c�)[I`��k=R朆GA�6��t��Ϥ�S-KRmɗ�L�h(H��Sz6U��29�ΐ���LKr��C���|����;*3Y]�b&#Z�|E����j�E�;YG�.�2ٴ���AF���a-K��Y���S�ƻ	V	����e|}��
����>I��K�XvJB� >���~gY��}�v6AD3d�I��29�-Z|L7�K�⯱З����\f?��#���jg�AK*���!|�PpNÏ�G��Ey�=k�UF،i�K�e����S���3��F����H����K_|N����`y+{��ee�U�D�s��/QF�r��0tڤ�d	�X`J��e�e����R#~
?�����y�X���C��vzq?m1�?:ػ��X�������C[��X���_��U�@��8/�PKMy�L֯ ,�׍��"I�YҢS5k��R��K��5\�\��Lm���7|����,i�֏gyW�s~����i)Y����� l��B���|�qM���%�>mdٵL��������d����_���[���i��[��j"��)�L���o����d�p�iLr�m�#[)�8kѝ��͔�6d��{~@��{�z��F���;I���?R����v|ɺ�u�u��أ˩xK���r�!���7�
�R�_��5ϲ�˚������]��2�S<Oi[������5C�F��A��2j��mc:���(��?�Q\��6�����w0���.Pw�/���*�/��o�2Ati�<e�d��S�
~���O5���_���;���-eT�fu�un15xc��������n\����v[�i,5��R߼#;��-+i=ܧ�����?�2��VE�������e��3f:�Wwtg>g�2�b	�M� �є�g쾌���h���T�
�%�
���Z{ٍ-����g�m�b^U7%�L>fZN,��`Q�t�s�V������*n�-C�{��	�	�1���b-o�-{^�(��&���%�R�P�N��Bi�j��3N��`Ul��7~jnq�&V;+7�u�b3���b�[���l��"�*b�&�����-�n'2c##���y�R�*ZxܞOl��Z��{��[+^	"Ѧ�v��`pڲ�t��,�����bSԣ���֕/}_{���`�pa�a�K/IŤ���c2��f���P2g3�R'��OE-5�7��,8�����M�%нZ��VY���h'	5c������-V��.�5���&�4��q�H��\O�قk
c.�����w�,%KU4�j�\��,��R�۸r�%��\ZګxC��ː�_�c����lF�Z?X#�6�U�1XBD�
��5^���t?N�9.��l����PH
nd�qZ^Z��T�Zy�cW�����e �X��&e���.���i�Z�v�	y��-�����CX~��\�>AS�\Mp��z>��.����4�u��9
����T'n��F�� ��Hb'Iu���؀]b'Wb�%n�����d\���A�َp�\[n^��kX}	�)q[�]�wy�vi��Sw#�n������]r/D�ě��g|����輆�������˒Y���U�l�{-w?{ ͼFl�@�m�L���H�>��YNIi�:�/���	��x\�ѿ��&��1TD������w\��(�f:�*��5���c؁$�1A�OVع�l�.q����K�-�4R� �͢�}B�>U�T�JO���Jcw����Ga�(��h |*�P�O�Kxl�"~����xW�=E<���C��ß�O?�">[��`4Pĳ�JT����S���\8vTN'��5�*E��6w�r��jݍQ5�X��Fի��~q�9��y������Q<���U�6��|N�K�@�.�
t�l�1�8D���G��G�ܣ��atb�8�}F������@Ӥ=C�iҜŗ(�|���8�zb��;��E����ە�O��}r��	y�(���w����M���%��uF�q_�������� �`X�n'�+w�/A(��6�����&�ۨ��g���=��Q�~�|��|���_�/w�,!�5���ߠ[�:/�<�K�e�P21�z-�]ôyD��h-��U�S�:��*���*̸���w�q���	'�iT�rζ�E��PG�ϊ�F�oN��w����!�^���[��݁N�u"�U��hS���?E|�;^���wD�|#"�Շ�Q�@w�P�""�(�Uʩ��4��O�I>=�t1l�wr�q��	l��]�OѭOs�{���$����8�~�bG�R�����83� �<��ô�I4�sO�1(���dt�M&��ek�u-�
�gĈ�B;�^��.d�o �S�!P��;K�x��1r�#�Νn2rS�p�5�̄����5�Eqc�����E>�+�7?J�|���qt�\Vv���)��b%�v1��E���U�E�+��K�:yr~�GT>O�}�-_D�g���&>�#"�Gw "6�gCD�E>�q�|*�-q��u=�7&��ɩ�hCr*&��̒SjGrjM��?J4�\����q���S��Ӑ��"�,}�9֏���_`�|��2(��m�Ÿ�+��W��	��m¨��[�T���n�h�n1ŷ )�-�M���aW�#���ױD��Q�'q��?PK   }y�X�y]�   �   )   org/apache/maven/wrapper/Downloader.classE��j�P����

>���lܹm���~LF�����u��C�%��a�p������Z��u'�B�D�,5\9)
u�l+�[���F�\�s1'~?d��~\#��l��-y�y[�*�|Ls嵔�r�'�/_�ej�G�¸�zw<���.�WJ6A�nt�@h�V�su��:��g�w���PK   }y�XK>8ڤ  {
  4   org/apache/maven/wrapper/HashAlgorithmVerifier.class�V]wE~���4]�J�V`)��Z%A��@J��B���d�lIv�f��
~r��x���xQzD�o��x<G~�zA}gӔ����vfv�y����y���x��h��ï � C�>�Ǌ����LN���<j��s�����B#GXA�^�6��ذ�T�8e�E�$���I�G73��`$�Lyڱ3���^� ���a��0+�3�"6R�H�1,3�fl�hQ�[����Pa�h���Yv2(��В���SHL�ֳRs�NV�I��m��h�_�a�ӔeD&�I��K
v`'Qꖳ�C�p:J&%�۠)�-S�����SS�6Z��}�(��D��}�ϰA�f�n�lm�,m_I�D������&�٥��#щA�^���3�Ŭ���ALVF�D��!�UW��a*�W8��׈�gg�Ȧ��+FrDd0�Z}L��A�=��u[UuU��eβK:yy�N�L��/�Dt-Jz���$uz�RJ��_�k���<��4���e:�tN_w�"�V�JM'���]A���^i�$9�*8�T->�1����ыď����(CK���D��czѐ5�պ*ZA�4"D�K���Nh��JT�xA��ù�Zu���4F9�V0�K�,;��z� b%}Z��[rj��|^��a�,��z�q�3�?;�蚎Q�od�w�B�`�nH�˗�!N/��K[Z֨��IWB��m�r�zg%#_p$A�T���A��OK椀V�uٕ��®���P3E�x�9�U�"c�jH!����q��{q��=��u�9C�;Q�[�Jc�l�a��251z��R�lJ�8�&�=*ﳬ��1�6��Ҧ�)T�����M�.Ǵ\�2�}�����D�H���H&�,
3�X�z���l����[�W_F�~���z.2�j�Y�S+�ah�k��mq��J��v�k�{ٕ�O*�8t�<u������g��e/-S�Ok�f/����U���	��T�1�E�^7�}R���Uj��Sg�35t'�+F3�9p��]�8Cc��܉,�J ��yE�q�
�=�] ��}�a����~
ŹT�<Z�B_�Y����G����j�1���y��N�|��K�J<�r�p{U�H�#�Gϸ|(�C�D$�p��7n�6~��j�5|x�I�-��0�kmZ���������a6�������?�͖᠇�M������O�\�î��<.!Wːyz�<�K�7��S��7�<�>��A�}��V{�F���	%�\�&��5�;���4K	�������m老)pJ�f\C��n�Y�`��;tn�L?�������u�^������+�V>o5M+?I�F~�P>u|�d#H�na��r|H�,�O�/��6���=�ѹ��r�*�-�x����!�����&G����"��,���O��3��>�PK   }y�XXW1�  *  *   org/apache/maven/wrapper/Installer$1.class�UISA��$$�Q�QԈ��w��X��@��lghB�0��� ��'V��Ej�KyPO�Q��;!@����^��}�_�y��ǧ� �b,�D√���!��e�W�� �%�,\��+�[y7��?3��.� s���rH�a�h��px�/s˕�5/a�ʥ�#nҲ(z>��HW����l�@
q�M�@'�΍��/ �9�P0��+l4���B.[Lb���mb�2toF��wDPuB�������Խ	
��[�\�:7��ك&�q��/������ar�Pk2�'VlQ	���MbP\=&��(CrY)�C�7C_������@��slU4�f8ѷ�"����E�Q����.����T����N��R��¨*2��m�g�)��m.xs3��KT>�F�$�Ӯ��@J�b3tm�p��ِ�n�J=���^�Ín��{L����(�����p�lM��T�� ���]|rV�]V}:�B��zd�K%c֫���l�q�'�9�ʄk;^ �r�v��p�yE����E��n�20�+�q���C�L�z K�U�U��6#y�V�4��l������ڵ�8��`4>A/��kV8�>@�bZ�U�d�8MjuXN�ͩ��h��������$݉(�Oi|F�='�����ՠI�:�1D�'53`�"J�S�RG�O։��NY;��O�|^��J4�8]�J��3Z��ct�sd�������}ž��8�Ʊ>c�^�fl-Z��$���z	���
��@��v��Kz���6�[����	=?PK   }y�X[/A�  �#  (   org/apache/maven/wrapper/Installer.class�Z`Tՙ��d�;3��Bȃ"�Y�	IPz��$�����`�[�V��[��Jڊ�<d�nkպ�v������vw����]�[�߹��$���*s�=�?���������z�E ��y����Q�B��}��&d��k���gvڂµ�p�^'�/_��^E���hw�1:{̚^������i
�l#2�w���,# 8�9熆$��S�L�13���v�Ź7�'����:J0[0-b�=���ٻב�<�����u^�a���:|8CP�sێ�s��
vǣ��ݦ������xbff��fǰaӮپ��9�,�0���X}���5��DK�T$~����0�د��X��s���c�:oMk�W0;u���V���y�2�kX���9|<�@�{#��U�ְBY�F�tj�����V�|�~Y�lu�Le��J��p���8Op���H�w#C��,��U��)g��O��c����["���VC����PP2�N1�	<a�n����*�vȮ�\�-ߩ��s��l}<b�x��n��Of������e���AG#.fv+��s���eb�$�r�QG��z�lF��	�6�hq(&�ɠ����1+�M5��ڪ��l�,	�T�uh�Ѧ��m[�E/ڱC��::�S� gd4[��*l]�p�%(�f�v7v�T�*�*�ԱG�WD(������`EĈڊ�SG ���Q��0[�{CN���6��xPaխ�Ar	�!�6��R!4'k-�Y��i�U0��^<�%8������5D&$�+2��Q��i�u�q@�K�j�����5nm��и���ֶ��KXU�?j�c�~8H��Zh⎬��J�e���O���J�B�1��u�Z��z�\�3z�civ$&�!�s̮+
�o�GǮ�qC0��5�*X��Ѝ�	�뫹���:���(�SH�'�Sf4Nefy�R�n��^"h��t+����jM�y*~>��~��S<�������V��u��tq��+Z��ѶAe�J�@0�Y+:�W>zXǗ�q7�9�RΤa��u`{�`0Q��x�'�m��tT���u�v\*�n*g�)8cT�m��5�;�d�<�oi����p,U�A�*�۵m˖6�y��FpE�_vvl�?`�1?k���
�F0�7��&Z�o�ph��]�p�o��vh��2�J����èˊ��Ŧ�L�]Sr&�70�:Н*����s��.��� �)�ӕGN�����ܘɚjv�m��}���b�@L1�����%0<h:�U�/v�X�P1Z���f�+hƦh0�� �<�Ѩ1��ۋW����/u����C�f_Cj�al�ΛEғ�1�~��5��`ޤ���7t��B��`,��������Oh&#WS�۩�yK�?��BAgȊ�~�R�!m�Z}�^��?��'���8Y��#I�s3��w0Y�k��
 <���ꋹ񯄆��pM��o�����Z����羅��G��^����?u��T�5�a'�N�Z�`�*�qC�Z���xq�R�x�մ��{���!�0���`�H���z�o�Ĝoڒ�7�OP����N��l�d*�������Z��{�sW���L#f�vO0�=Άq���Xq��U��I�d�
�P}��C�M���(O>M�[�a�=^��$_���V�l�7�$m�Z�-��w���=)�.n��y�[	D�եHx�,��f��w	�1�/���s�t�ܗ)�m�.����+���33E��cD[ͫ�f��T���^��/�KVz�D�i2G�l�Qz���O�3�L6�}Fh�
���I��Rmy{0T�6k1od�.�U�ʛ�<DT���\5�[M�*�6,�e�,!��V�v��͒�����Y�t]&�U>v�Zqu�.RIӛa[��7�Ah�{��u�H�e�&5��� �4+o�l6[Q�1d��kLm[�˹�](R�BrA*[6A�F
2���	]�W7��x�r�.u����7Q�d�hra���a��C��lg��pg(`�5���Y$�i�~��ň:t�����]w��0���l��΅Wʨ��70M�\*�h�N���ᤊ�`۔n�9�����ͺ��6���)�۩�U���������
5�2]�I+1��]�������+��&홨9ʏZ�-�t(0H$�[v����^��+5��6D>9�"���i� خ��6VpܲW�Na�54^\���mOo?ۚ�ook�Ҳgk}�F���6b��I�W�)|-����4�k�`��h�͖x�^3ڦ*��IܶѠzON�TU!TN��g�؍��}��GY��{#�/:���O�����c�D�3����`쥙Ƹ�y���Xc���f�&~����?��t�a���YՇi���>8wؙ�o��ϱ.��ܼt��l�XG�x��ܿو85��BeڍB��4���c���
e�r4�P��,�������9����Æ��ĵS���dU���J������KG1o��S8��h��Fw��^�jtۺ���<i��HTh�T�K�)3Qˉ�h�3�;NY�4+4S9����υ��DI՘ �����ԃ��3���N0��۷��Vb���]�����\2)��v����m���N3q��48�P�i��p�`2�f��	I�����p�����b�W$�sf:����ZLL�t�-�].>gWA**��VQ��UT?�Y�8�'��-���b�P�0k��B��lEb�e�?��s����:;�g���Nο��L��A�n�ϙ���`������4�Q���T<�Y���-��Zm��.u��������Xt�:�pQq� 6(�KFp�`[�m��׸�ݱ/�cwT�	}Zrާ��
�C�'I����C���F�J�U�<#ؗ�ǧ�^�Ց�4�jFߎ�9W�q;��z*|�!\S�-uA��~��;�a�^Zped8ͧ�\|���>[ Eq���W4�C�t�}A�l��S
|Q���շ§ᡣ�_M}Ei)��Gիg�����)Q��u��xO?M-�:�}*���OT���#x>O	4�Վ�`z<<����d�xJE�aa/b���7��F��%��F�¥m�v4���.lA[1��p��Z1�6|�o��st�m���{��Ӎn\)%�+e|������ގ.ٍnك������'G�_EH^@T^�-?A\~��N���x=��e~y�a��9�U���#K"N��A��(��+���،����s��B9)8*@��H���X%�� �i���oK�\�N�oe�|J>Mn���_��k9sg�`�)���z���o�Vj�4����������9����P�>���Xnt0C�&Jz�G��Mi�[!W����6W&��0�*��ZK���q��q�L��qY�+H���0�*_�~��GO�Y�2�q���b~�����W��\��A���:Q�ϟ���I��>>qR�� �Q���)ב�z�~��FF���sw]�����j�xJ�Z���W�g�V�����߱1���y����m�O�]�5e�s�������CZ��=�5k8y��X�)oJ��=q��GnL��2��~*I�@��@�χ�:VY�/#��<l.~g�n����8�%-������t>�1Q�x�Ϙ�y��ݤі2��;ZwR���z��^��O�CL��P����B�C�����]��֧�^�v6+����A��y/�H�i�ݧ�o�&��5��S�_b~R�f�95��԰�i)Ose�L�O�+�ס9�Ct�,K�I���FsH� y�{{���bTQM�<.%CR:,sw��af��3xNO�9]>��u�|_Opϻ�sE\y�jD����#�P0$g���r�
#R��q5Z��յ�լ	��q���V�i�#rA5\|\�v$}\"��L�$�;$�P���Qx��c�s�����xX6�`q����s;�������/c��=(m*�<�uv�U'�{|^�t�GO�+�b>���Hg�-P��g��Ǩ�!��	�3�q>������x?M'iSx���)�e�އ�6#���	������	\���v�D��'���%��_ë��~�8���>��r�<�>���F �����Tȃ��2�!_�/9��Nڝ��CNԪ��*j���e�������[����Oa��\Y�I��ئ�j�zO����$�:M�X�x��$x'!J��i��	��%#������4�
m�H�*�ic������:�Kdg��~F����б�Jr�����cN��ǹ*y������PK   }y�X;n4GR  %  %   org/apache/maven/wrapper/Logger.class�S�NA��-l�]n�� r�Ee���ŀ!�Ú6�3�aYlw���Q|0Qb��C�nT(�Ϝ9�������/ �WATALCzw�>7jܵ���]Q�z������l)�8
TIhS�����a���p��!ӳm!�������6�ǀ�AC���>7}Q'
o�Sf�q<�t\����\#��Zqu����漫�
�
F5�a�a��#R��m�|����)j�*��vsٮ܁�	��쨐2�)��J��Fq���%G�4C�ҁ��r
�q�e�
�b�C{D*��kX��m_��]dg/�Td��আ[��0tV��y5�]�jp��n��&C��Q0��+�{�-!��Etӫ�Z�K'������4���,B�Jwۻ��Dc�7��B����y�S�7NH����j������ɪXu�t�E� �.-l�V�a<XY���N�N�yYF�g�;�!]"B�Fq�N�U@P��&p��M�uRw�F�Ч�~"�.�h}C��EC�~���O"���Q<�#���Z�&�'�/������C���a3�f;`�6lOO{�3T��c���P�ٜ�<F�;�PK   }y�Xb`3�N  ,  /   org/apache/maven/wrapper/MavenWrapperMain.class�Y	xT��o�&�	F�d�	�"	H��	d3��5>&/��̼a�M�V[�.jkKWk���E�I0E즭ݭ����k�ͶT���&��d&�������{�9�Y���=�"�ω�*�S����q�6��Gk����@~} 0w�W�;�@���B�Ft�V�h�1�6������Q-ѣ�r4`:�@X���ګEJ�+ڭ���v$�k�5s�N�_�b	��&����f@�	x�3��*ʥ�D�R��a6���LE)�,N��ף��A�ΛQ�:�ό£u
������Nq+���j�e��֩݉c���k�%)<,P]>��'v��"֩X/5v��=��FlR�YE9*�fuB�1:���y��A-3mM�V�JA����A��h��CE����3����`��e�-*�b���-�m�,-Q#������ۡE(p;.Vp����.ʴ+m������<m��:��%P��+D�f�W^�=
T4�I�<������qӠ�W�сt�P���, >W��W`]V��p�ԂA=��r�KV�f}D��f�p8hhD�m�Ih<|؉��]`s�d�N�����j�����c!\`$ ��Cf�+6e](����A݊^��^�x�,���m5B���*�r�/�_����f�=x]v�M`���e�����C1��}�5屮D��5*�p-��n9�h	d����$�� O&,�4d]�j�#�����p{ �7�q�>�&}�/)+em�L\8,��aLE 
�Fx$0�Rֶ�Q����!,�>4�����]��q��|���,V
��8�"+�	x3�%[�S��ɣ�*�����9������I�$vg�8�x��x)�%�&�th!��
^�����t~���T��q��<{K/�7�����\��D� k塂�|��������@�U*^-�F��cZT��z��;%��T��;��.��xk�U5��^O�#9qoT�&o�[�VK�jT�5�������B瓤.oSq�����}�A���P�N�»�̮m
j��m1�ʤ�	�f#du`�f�v�����hm:��^�O��U| X�0��4úψG�D��y[��R���aQ�Q|�e2��vï�VgV�lu������>�O*8��>�O��N22M���m޲6F§V��|Gæv�{į[YA����Yk���f �'�xS����MP��zǢ�a�5��ǁiVR�H'N�a�U<"��T5�l4��|�gU|�'��Cq-Kk��\qe�c
���Kx|~إ�d��������mO��s�+���p�0��:�u�)�헐�0v�l[܉`t�Kw��Ys��ZLwo��\#U�����T��!��W>��xޟ�<󹤉��&�p�I|[�wT<����Vʥe/9����c�����6T�u�$��Q���j�|8���Q��(b��$�{�?ď�P�atJ�6�x
]c�\�=;R��0;V����1[�Y�6�Q-��l�ρ�
t�(����G�xx��|O�;W>~��W�5$��Y|��+��,���}m��S�;���D�>ʦ��9����4�	��XD�ˮk��_$o�Q�W��y��ŪØ�_�Q��w��MbG��[�<	&'��?�Kſ�}�$����A#�+����$���ee�i�H�Y� K�"rRev�D�1��xp�=7�g:a_:�A�� ��u:Km���D�m�8{��1���sAU�t4�{;��|ޞ�֮/g�bU,���'.��xb�*\��w�foKC_{�P�2^�2�O
�۟�ut�~�LKb�nR}�X�R�J twuu�tu{{zۼ�d�LIg��e�6��`\V�4�g$����<}���oϨ��C�ޞ�.��!�mQ$kgCI�fH�>�@WO�Cl�Y����b�s�M�VY6i��u�C�h��<�L��Z4 �	b�9 �+�������[�U�K36���׬}I�i6�v.����J3���SN�Z{���,�L�GL;��Zrƾiط�����s�qߥ�������A�|�ѰfZW����kI��4��)�L�V��N��ΟCL�d�8���	T�i!գ��yqY�r2�ޕ����v�xt�m���e����q��\)V���|�r�4�L9�.kf����ڔq!����K��xC�x#ǛRƛᄐ���RNJ-G��X䙄��b��3�"���� *E��ZԐ��E
�����^9�L����)��3o+'�ҵj���1���a
����y�p�.���Ϊ)�ޙ_��F��4Z]�ey��;�
�F��z&�7��i\I�U�zVՋQ�g=����b�A	�AO׌h�l+ц!t��;�n�h����V\�w�
�B�W�-�ʡ4%�ʥ�kQJK�I�b��F+6b��Hlg��Z/�%��Vl��{r.� ���Qq�4��騬��(�]7PY噀1��#8|"y�R
|�I/�чe��JXJ�m�	�X+.;�t1��:QO	�,9��J=�b;z���7�����I�,S��n �3�p_�"���j��*Z�l�P
06Z�7+�
�fu	T-���+f�'�v E�R��RВ��&iTJz�rK�k]wO�'�֜yGR�%J�%��D�y]�	�}���	||�q�9VNb��!�����=X%����/�B�`��7��N⛧i�o���Y@��D���"DÅ�!�yb�:DE�&�	�=8�����L�ᨄ� )y5*l�h"��=
8#�[��z
.�r�Lb��G�k\����>�c(��3���L�g������y{%��J�o&�ێ*��𧁪NϣPH�G��v~���(���B��}��%"�3)��"{��F�T���WA��;7%"�'���˩�X���73*o��J9�q�\y=�ކ�n��]��͌/�Q��Y�\��^�B��4��	��#�J<�6(خ�����#M>Μ�r8�5^t���Jfl�+ma�b�D8��S�Y"
�D��,�_G�'��M��$��ú,���Dr'7��㻓Tx�^������^�労�hMf�K-L�%b9�NlR"ʬ�|@ˀ�~����+���
���PK   }y�X���|�    >   org/apache/maven/wrapper/PathAssembler$LocalDistribution.class�R]kA=w��vۤ��֪� %I����R������mv3M�lv���-��?�?�xg�m�A|�g��ޙ�����+�xpPrQ�QA��8"�E2
އ�2�	�#����Pj�5,������:գ@LE4��D\�$���t*u�A��Y&'a,��~����\�p��4!�CN{J��V4Qip�bi��Fh�Gݨ��/jJ�o�a���M��P�D��&�ԟ�Ch�yB+�/�r>V�M�w:��#����6Z�		W�Z'��{ә��<iޒzi��]�Hݍ㙋�����voX<�v�8h�[�iޔ}	ğ��v���惰�w~�:��p;�X���Z��`����#�s����iO62��x��	؛�J��o7ͫ�m�9��hH�,$��%�
���}�m��-�	���PK   }y�X\�@j#  �  ,   org/apache/maven/wrapper/PathAssembler.class�VYW�F�$�X�$��iK��p�f�l�%8eR��T�+ؒ#�@��������+=�š9m��W�zzzGR��vO�iF�{���e�̟���;�����
xx%T���ᒼ GR�6�_Rf,�����Z�<���j���P��E7�"rF�I*����h�EC�d#2*[�n�T��b0��'L���
ÖAې��Y5���]>ԡ^@��M�3��?�=�6��e�Ru��~N�zU�2�x�Yi��v+21%"�H؊ C���MeX�v�K��v$b����wIh�6�e(i}A�[�ʹm��5ʀl�p�%��c�ap�������6K�;6o���,Ct�����sCE\��-6�ЊP�a.��T�A�tH�`��X�A0SO-�P�FYF�$<�C�}:�f�[|���������9�ÜC��N�÷W��9U�Gp��c�dK:0�3pB�I^���ͤ�S�6�z�'�_�iP���Lھ��U"{n;�g�A	Cf�
7��$~=zB�;EԇQ�0&!�q�Mk&�����Pc�ʒc�ak0Z�EL2���)�'��lZQ-�,��r��\�	�(C������@0Z�
��1	2/�[韑��U�{E?Ł�T�>+a�ˊ�� �P�)uO�OO��ƦF��8�y	)���*��r�����]��E�dytl�L_ϸ����fّPLX�x�)L��R��QC�R��Xbض&3��,5M�jF��b�8C�� �ʪ��B���#�)���Eg3_xF³x��x�k	�˷��\HJ�İY5��ڼ�/jsևE����+e2HYl�tl|,:|�B�<�0�
MK֬I9���s����:�Y?�j�p6W�q�6,��;vR6T��.z��J�
����v=h,݇ȑ��@)���o�ၘY,F��8�^<,�	w���'�=�&I����m�╱�r��S��5[ٓЩ�0j�1K���3n�m�	#E�q�4���1���F�Y�o���4bMm�T(G���@��H��C���x��W���F��F]dh.�݀��&}Ehd4V�r`?٢oѻ�^܉��-9x��ȕ�s��I��q�P�kuD	t�zтj춑����g56:y��.�_�!�x1^Am��Bm94���9������尓�ݫW��k����^����?m�8D��n��TSeSU]�y�D�C����Hgf3]C8@+щCt���yg鋻1�̻q���_�q|H1� �a|D3���H"��R7�����'�T�$]�\����˃j�?>����)O{�z��� 'k'l6!G+�&`[f��� �Z\��T�3ֵw�F�$R��70�f�I~OA��<��O�I�C)I�<ف�b�m�)�پ�?�^��s��i�ހ���h0��f.P���u�����[�I�Θ
~�u��q�/@��=)��*����ћ�o
-~�zC�?�*œK����|�����������܀��[��ũ��2���'W�4�<��^+x1�������.
Ք�:�Q]İ�z�z��=q��J�������aT�"�G��PK   }y�XR(��  c  6   org/apache/maven/wrapper/SystemPropertiesHandler.class�ViWW~.$�Q4�-�$,q��*�J�Ģ�vH��H2'���n�M�jw>�� ��S{�������ԾwBHb��_��y���˽����g [0�B*%�d��`XzB9�������a��ѣj������3*�ɰ�J�jܷ_I�����RͰ��i��;�ƹ/�j�IS�5j�DB���X�P_,��>)�55̣�bæ�@�ܐb��G�s���Pϰ<ᐡ'�a�<�B���$��$��A����*�%��גi3d\I0�[T0yc~O�E�;~F��5���ו��VV�3�B3�J�GF��N}){8����u9+}#1C�T��܅V�+��W�DB�d��/p\��yn/t7�w:̭CI�dX�WNk���|Ê<��T��޴�pÅ.T:�����0t�y2Ƶf��4'2�,��)���؊mT�J�p��;#���\��/L�&�E3[����у�NS�2���JUH������-�m0��WF/ \3���~[�sa�/���؇%�я�(�E\�UMn(�n04�����@ �21�PW*� ŔT��6�>?,4�e�0BE�Y���(a4W'�=��}�(Uxc�8��t#�S�J8�}	��|��H��M�L��w�~E��9T�32�7dp���Q~Zt @��c2��pQn�Sb�Qf����q<W���t��bRl�vsa{c��'�\s����@V��ĠJ8!cB�[�f g�v\�"B:!C�N�5�t�����la��!!%�D�h8����ZԌYI�(L�8-Pp
�J<ͫ���𔌧�LQ�P�H�	Î2�q�n�'9�ŕZ��Ѯ�.�G�����!�Ik�ߡuˤ����#�VK�Ӊqn��Ρ��a%>����'�̘J7Ȧ���4�TO`��eXWv�FL�E�i�Q�Pv�30^�ˏS�UN�)q�l�|���wї��h�]^���rSVԖ�ʕ,�������/E��J���/^�b�l��Z��sǙ:Oz5!S	O�����aWtz����3mP���EG��	���K���6�<����T_��� �k7z�
�˄�݂ki�V�;�n\1FiWI{z���e����h�{g�.Ӧ���a[�*}� ^��V��IR�\Q�ڀ?� �͠*��	������~iuD\> �W�"�љ���_�B���h��O}�Gmn{�ݶk�hΡcl]daC��6{3؞Aw�v]��`�,����8�p��<��vۮ��6�G2x<�,���Y2m�e�v�$ϣ�����,�YLU���͋���h�6z��`7]�����0�t`O��m��y�@��t�V��H���7���.�бg�|���Ľ���\����0Y[O��"�J�j��x�v���=J����>��vRTP4>>�G�:�>�'��5��O	�
�#z#�Y��H>+�9I}A� \7� ቛ�J�I8O[	u�1	�7)��B*�,��OT��m��*	��1�y��*��������**��g���p�U��[G*�X���;Z]D=H����PK   }y�X��   a  '   org/apache/maven/wrapper/Verifier.class�P�N�@}.%W
�&F6�$,�*�UAY�#rӫ�%:�~���.:�0X����������a��6�I��L�N?yS��u��N,{�:�=3�ѹq���8�N�O�ƽ������?$l��8x�.�u^��`\HL>��J���%D���0���:[��Yߘ0����� !4/�S.�o�M�Β��V\�}ʗb(���|��F����UD��T54@�&���.�%���mPK   }y�X�W!  �
  3   org/apache/maven/wrapper/WrapperConfiguration.class���V�F��!��U �s	9PJ�ӠNM	�����>P�&� ��eɰ�3���*+Y�����G�-���G{f��g������ X��at�N�
���0�_p���5{�V?t�_��3܉͖B�axj�NT~�Ou��_�zY���zM���M�<6N�5��ك0�ĎVJf*{�Z.�ܭh�=m?_)fr���0��OA?Z�W�������L,ݴ���du��TQ�0�`#���Uչɠ��7�aY�r/BS0�O)�W/��]4i߿0�r�1LJ����e�Y-!��+x �����uiV-~�#�N[!s�9�l[?;��|���U*��vv')r�`�I�#�&惺�{��:C��~z1�xO|��Sm��L��U*�0�Hni�t��H����b!��TrZa�a�+a�zlTu�yU`�
�Z�ĪX|�`A0�~5���[R�,�܍�W
��kҽ9	������r˽P�&27{�O����|��'��m�d��p�Z�6i:%^��\����:7�#Z�O�������Z��Z�5C��d�sj�j�]�U�ɰ���p�Z��m�	�t�ʳ��wsӐ���ݚD�x��V���V�H��GT��zl(&�{�l˜�7L�Z�?�H����n'��Gc2o�>L��^��a7ފ��_� RS/��m��y�^;Է�㲣��xD-�A�o�ޕu<�7du���o��	����~?�G��H��隹�o/���kG(����uE�ƕ�;�x��=>y��5F�щ���xt���?�Ɯk�{��n���O �u�!0�{�3��x]��۲I��W
	$id��w>�J�X�����I������F�6R~�:y���)��.Q̲�a���3�/��(��d4�\
�R2Y���6 �>H;��� �$��-�'�X	�$)f���%Q8�W�� HJ
���d:�C1�6 3n�3P���A^IAJؓ��A�Sj"n�(��T�� Ⱦd_��|�5żi��+R�*��R��,At�9n��+�T�� �!y-Wd5rF1f�5_�7nL�PK   }y�X��e��    .   org/apache/maven/wrapper/WrapperExecutor.class�X`��~[���e�8�0D��BH�;�DX�����g�,	�d'@�-�{7-]��J-��(�@K-�t����~��$K� ���o�����#�}�^ ��U
��x��2F��-Ƥ�h�J���n��w�eX	>��a�{;;��#Y;��c��ɥ[�Q�7Z�J'��-33~�j�]!x�1i�$�d˘7[z{���63���I�QgC�H���촕om�ΥiհP�(g!k[�ނ� `��%*��h%,���p�` �8J��Õ�F+�km԰Lay)A�J��I�V2�
�a��cp�B]龭|djI�c��������]��	hmة�x��g]ӗM�քٱo�t��bN�p���8Ia�!���g}}==���|�Տ5
M^�C����5'eքF�҂�)&��А��hӳ�Rs� �&�:֢�d�$�1�LO����\5�~W�\D>�wpN���S���>|�5�ΰ�P�oI&Ƭ�l�p��k�5g�8gQ�gY�:�F��d����T�zXai唓���h�f����%�����G��]G�d�<'J���tl�|_O��5R��ulG�*��f�H��I[����@�b ���3햁��V�G���*,ʘv1�p��A?��e�h�f#c
�~d���r�u��j\�c;��3f���~X)e��X2�.3W?���W��c7F������pE�$4u���~;�*�X.ձ�9�!t̒j�Qh�`�	��tBG����J/�JĴ�~�����| mY�#@�vZ����}��@�0���E�y^���@�>?�*�=z���5��� ���N��N��Z��u\#r���V��:jaħ�����vo-ހ7jx��7�-��Y?nN&�������u�a�9�t�`���:ށw�9k(ch+"�N���:ރ���V�!:�S��>���i{r*!�+d7����-)��W5|(�8��¡��G�[����޲94U���	��
�~T��&��#�U��tr��M���8n��	��-�ȿ?c��$��LL�k8�_^��q� Ԃ�i�:QM��܎;4ܩ�.I@�sx�RN��<aJ�}V�ݒe2�K�K�B=�cFH�{c)s��F�����8$�klJ����r��w�u���EE��t|A��:�r�}Q���+�1�hb����!VW����V|�L� ���H;=4b���bI!:�J'�=4i�-�xH����k�:�K8��-͑=��m��G�-���T��=Ԣ��q�8�cI?���Z��'t|O��O2;,)�~���5���q�z�S~���Ñ���x-��pgg��ߎ_����Is�'H�k�V�����:~�ߋ��澞�Ô���E�?Ɏ�r#����:��i?b�~���-9��k�w��CR�ƸJ�G�O�����tB�9Տ����H"=-��-�7���a`g[�gu<��	s�3���T�4X[[_T;9�,�\)�C�(��*UM�*�O��N��Q�4���/mv]�]���x2cj*P�N��Z��j�b#���eS�D2�Hp�Y��j�T碲�7���Q����R�~���fsg"c�$t`���N��~�m]�V�PGkj���Q����YaS{#%��M��j2�%�[�l�|Dˍ�B��
�j����K��.+�!U�S�ں�đx�M�\C{��n��q!���t�tF��kV�w���:7�w�t��{�zz;���
��b�^��f%�A�̋L	��m��"�vv���{�:�WJ�z���U&+[Y^B��6̞`86��j��h��������SUof���m�vf.�,t�\>ڭ4��4q������O�M���$G�E���ى�f�_r\<(��A�I�4x:� ��1�-�,`���2R��o}�Bo �{,6��/E�j&s��Yr�;b21��|#�33�lz�B�^hAAtFS�֬,^��'��+��"��H}E���b��)�]L\(�S�S�/����ab:f��b)�0�aO��g%3(Fz�A�� �9��
�_�xޤvWĘ#\_+*�#�ٵ-9aJ��J��G�1#��vͤ�ĜL�e��V��*�~a �B�V'A��9��S�)_�q�h|�E�K8n,��8R4��㦢������"(Ws~���y��{j�Z��pԆ�e�P=��9���₡i,�F��L���<g�&��O�%Q�9�k���4;MY;���,���Xy�݅P�d�=U7�f�8�S�s$���vD��s}؎cx�?��=͵G�.��N�k	�]�� ��p��'�w�e�Mw��1r76E\Ŷ�c��lm�,W�љC���@^9Ħ�#�]��%����9�#2��&������C3���w箘C�Jw������׹�k���9��S3x[�rg�ξ�f۝��]Fğ�Ӹ�;��x?�=O^�e|�O�l�"l�Q�����4��W��}��g�{Џ+1��� ���؁[q!�g;�.&��w�O�����Fr�!�o�z�A׫3ԙ�O:�RΗ�b����z���5��ט�J�W��^�I�*�k豏���nG�RwCAX�:Kҟ��id����i�f�[��>�0Vұ��3�s�9�~�h~>�{����M9��>\}2}~9�G8�Hߘ�7������X�T����Fs�qy���9��~��W��݄`���z_����7�<���������!�>�-��fwaO��-�n��9Fk�Y�Rz`/��q��ŀ&��'�)ZC���Y�L�k
WDn�`:\OncL�a������V����M�lΉg<���a�M��pj��j�?��"�b�j��̂s���6�ߖΐ9��������1�,�WU�BWp������� �K`O�����4��߾�����A�{�Y�������
�B-R�Z���ķ���k�z-ׯ��נ�;��Z����v�)�x�*_�׮SZ�{�d^Rk������b��'�Y�i�M��/�/���|q�߁���I�(r�ߋ�R�m}�G_#���7��;���_�T�lr�麈�n��N�*�0��T"U��j��:R��Ku;^��K� վ����@����g@+��~�1��{�&|�����@M�y�?D�rM������U������ȴ:�����$������B���k$.���D�W�)� �~)'�<��A�ꮒ0:J�s^Ty�]�N��`{�'P�?�̼���PA����R����PK   }y�X��   T  ?   org/apache/maven/wrapper/cli/AbstractCommandLineConverter.class�UaO�P=�ut�!���QPa�HP��$,K�fW�(�ڥ�@�����D�?�x_[�����@���}�{�}���� �c[BB"�(��g9��4��Y�c���]���i��6CH��DD�m��jeM?1ԒvfX깣�ˆ��ESݵK%�:Θ�q�9Ñ "$aQCQ��0�fG��|�u4�m�ڵ�3�q�a@��/f��kr�71�x���(0��<˰'g�;�'-_4R����qE�w�0�qL0��/���"[q��s�E1����q�oU\��I��|K�4W2Ð뙿���Y��n�o8@a�>&��<}P-�'��f�غV�i��߃����T�:�L�a�&�E�����Mg��mo��è�)P/c�6�M�Z�u�ID�tC��uL����Ս�k�VE��]w��l�)TK�����]<2��zw1ٵ�89wg��Sɰ��M��QWSN��ҩ.�6���^���}Maُ��٭V���T�C�2�ґ]utc��?�i��9�0G�<����a��i����Pd<�\�}�E������xJϨ_�gxNq�Ff�"]Fȴ��]PQ��	��>(�$%�T�Hc�u�{^�"��f<�Yk@�Wq(�0���8�8E�j�^���eZU�Ҫ/<a��C�|��,)5�j��ljv���Ku�����mKs3ߺǷ�}�(P�F(InD�����;�@].���B���U�'��%^[#�'!O��\��I,�įM��j7�z(�oџ�J!��7PK   }y�Xm�v��  -  I   org/apache/maven/wrapper/cli/AbstractPropertiesCommandLineConverter.class�V[WU�N3a�0���Jm��D���Z����p�� m����83����껯��Z]�����A��}&�-��M�b�9{����߾����0��%��O� Ï&��Y�+e%���Kʦ�ǷL�\V�x��ŧV,�Tr��Q*)z>��괡o����M���ٷ|�HVD��}]�T�EE/�3���	ZD�2Zq�a�D�L�D��Z��v��=;�e[3t��H��x�[o���Mw{ͨ��ռ��pADHF'���x тbZ��!~��I�T_���+"zd\ī���˄��XSf�RRu�b����d�r�.6��ʙ�3�P_�qWΓ��f��"�)ގ����wkmV)K�OB"�<n��r��?`����z��16���!oa�a�,J���R����n>;n�5�N�P����q׉�ïU�b����N�\9i� Q:)�&�l��-"�qNݶ��~�O�#c
������u5g'�a`7%��#�]�%E��W��WB��Np�-HcVĜ�y,0��C�0�r�L^{-z%`q<���>�kUx!㜟T�a����9��;$,�x��-����-[�s�M�('��ޏe,�"#Wk_�S�X�Nm�
̴�'m|�\�������"I�i#�����+�5�,y4���b2�L��j��I�G����<�,�ϲ�H�N{�4G�J%���+��Z}�&�!�Jn%xbD"K��S����Z�S�A�)J�ڇ�:�7��^$x��s��p�wʻ�;���[�m�	W���Ω5�"v��8���-�;���?��	�8kl�g�wL3��Ѭj�y:��T*��y�2lT|�gr�;(e���S�h<�ѓ�� �%\�?�f0~���cZ�i�n��3��ir
=�aV�)�6 �< �B����Gڣb�UZ��>����!.��T�Km�*���*^����R���!��
�p��P}��w�ִ�8���V��
��i�wv��a9��O�[z�7���*r����s�ٳA2��d���͡	X�r�U��x�9F�h����4y���	�[NW����O��e�9��a�[����W�~������}��1�Np�]���>�GA�F�N2E�%���ԣ�iQQ�Y�uk�{�,��:�-ÄE~�{�rI�$�4A�"&��W ����?�Ֆ�a�%��"��'_�������>E6?w��GR��^ ]ţ�4H�G�����KY������K|�O�PK   }y�X�:�dP  g  ?   org/apache/maven/wrapper/cli/CommandLineArgumentException.class��OKAƟI�M�,K�(�[i4�.a!��!�������2��}�NB�>@*�Y���C30�����3������%l g#_F���2�3���T&"��.�J�ZH�t	j�η��(!���Р6�H�Z�X�&*����u��-��Py�E̝p�)�t�XqE]_�^L�!���Ҁ�de�|/s��I?�����AT��e��)a�e0�LDL�q�ݱC`<�����uO����X'kk�LPꇩr��05��ua h¼�Y ��Y�^W�&n��s��,��υ�X+O����B�
v3��=T5ð�K�=rzv�}>����E��XȾ`�f�}d#ֲ��'PK   }y�Xlk�I  �  7   org/apache/maven/wrapper/cli/CommandLineConverter.class�R�J�0>�������7�.4�
���L��в��;֌6-YW}6/| J�R���z��|�~Nr>��? �-(8KdHYʂ�1�Q�W��%"N�I31p��n"r�J�C�31�������m�~P֏�i��9���[�i�1��F�z�872�(�)�@��`�X"�cS��L��$��q���r�����8]�Ly~���g�OL�p0ӯ��rym�[���U\--<�����g��;�GfF@+J��Ss��[�:ƹ��G���4�'V� ��|5��B	,u*��*�ծkY7 �4 [i�m������=����7PK   }y�X��I�U  �  4   org/apache/maven/wrapper/cli/CommandLineOption.class�VIsG�ڒ=�4^P�؃	K�`dC����,�����IFR#��(�K�?�%��KI*6E�J�r�?����T*���-���p����{�����?���W ��u=�)��E���XK�YL.�6y�a��Mݙf�Ec�
�\���ɛZe#˝ ����`@� �NYv1����O����L>��r��ɼ�'�V�����n�Ų�[&�b��
�P���S�؅���T�`7�`�䲥�K����L�"mh�JJPڣb��R5�X-q�YzV�!��U�����m-gp��.��e3D�1�9)O�0�#
>Pq�vP�T(�����:n�ք�	"J�MW<����zJA��Y���b'1���$���D�%cw�.����N�\��^qDvΩ��y�`����jB�dZ=������Vu��� .�ʟR1%�B^�ۺ[Oq9��2�P/�Ƣ�.bm"	�񪊴�x��yM@�h�Ij��5�q�<�,:n���Ŝp�1��h��� 2���;м0:6�[�o���O(I����ȵY��� ����7���ᲊܣ�q�����p��z�Ԝ�M�v�������b��ç�B�Ǜ͝NŖ���V����9[��r�^�5w8c�5cY�uq��>K�����i�3�6�-�Lh����J���w	~ �h�G�ZY�&����D�ȝŝfW�<�'��x����|Jΐ�;'I�@������pI+W�,�h�㼼�c�8H>gCJ��y�jPD��N	Y�&�0��������p����=��Ɛ�[�h�gR�ZU;ϯ�Α�HN	�8B��C_�>���t���ы_?ߧ?z����\�r���u�t{�)����;|��z9�,���K�o!�	�[�$^a���W8��hs��7��?#8�
���x�׏���}���@�N���aD�8H�s>�1|F���t������sh��ny"X��JkM�i������`�R�_!�M?�ȋ��]ƕ��0�<p���q�E�W���l��� �I"t�p��o��b�#�	V@b1�:a��q�0&;b��'1���mf�C0���2iGJ<���m���Qڅ����P����:�?%��)3�h�ԄX�qi�֙7�&�1�����m�\I�o�;�4�n��Rw�9���f]�m�T}.�Ij�%�&5�u�-j�N`�Zj�
q���g���%��T�j�UOh\��P*pdS�?��`��#���j��M�en�2��K�d���&�`����p}��~��� k��y�9,����" ��!g%䐀����C�P3​��s�3mo�ͣ.�e�w�?!�)q���,+{�)��}ֱ�gZ����_�Z_�PK   }y�X�#�ر     6   org/apache/maven/wrapper/cli/CommandLineParser$1.class��A��@D�k4�f5�Y��V�†(x�?�OL�t�nu�6��V����^�
�r�?��#E�0q�Pܰދ��$V�yn�J�R�]]��W��-� �{�"!d�X����V����������e8�/���ƅ�k9�]��O���0\Z+~n8	�m<�F'��.��	z��G(:*�� PK   }y�X�@Ƀ  �  I   org/apache/maven/wrapper/cli/CommandLineParser$AfterFirstSubCommand.class�VmSW~N,,�	T���=
$��[���T	��>^�mXMv3�E�#����ؖ�N�u�����I�XR��v��ᾜ��ܳ���~��g g�$-bHP
����u톺���~��M7���g��FC�;e�w7U�r�L���W��U���l��e���e�3S�g��z���N�Va��3�0���]����q���4c�k1!�4#/��*�R���\)��[��[��xF�r���\�3�blg�w�=e�E^�^W��(N`���N���+\�@͍gf:Zu�����׉��%x7FBx�'��o��~��w,�`�O��#���R�o��p��8�Ot�g�{ӯF���b�c�s��d[w��o�L��]닪�A�H����4�˜`��1�ퟱ�Ә�^D���������rG�_<�d)��o�Ҍ��ƶ~���L�G�o����.1�oW�[\_��DA�^w��`�B��R���4�*�: 
vߨ�f�v����
TaM�}�:�J�g�����4ݪ`�����������%�V��y=,�aI��
�X5�w�ӉN�Y�p�U�w�R]�Z.��x�g�d�V+8�W�������+��W��y�qA`��/\��{��\����"=DC���{O�i�N�j�>�8����~����}#��{���OP8�y��A��QY�"i��QK�h�؍I;��Yt�?�9�稝<�0����'0~���q<�_0�1�+&pj+G��Yy�����;��Vn�����̹G��udڠT0.�8!��ī��G/�+�.2����Ʃ	ڹL�0�2�� >�\!W�� "/Ҧ�F�+�+�Xګ/ڐ��:֐_m�(�K�W�6�W�5�aq^I&�I�:�x )�L�X�&��ly)�Sn���η��PK   }y�X�wM��  0  A   org/apache/maven/wrapper/cli/CommandLineParser$AfterOptions.class��kOA�߳-ݲ-�� �Ze[/T��JL*��5�ڵ,��d�\�O~�x!���2�ٮe� Yi����s�y��ə����O �1�AADE4��۩�bU���.�K�p����嚩O����*E�2��0���]�y�ꚶ� ���#L��	�+\a��!�N�T� L�T�l�]���C�owϳ�N�EW�H�)�\���!̄���Z�����~4�qL��8��HqY��&��>�:�U-�66)�}����ㄊ�$Nb�0N.�KT*�7]G�Dm��}��`�D�N�~��^�k�E�y!kl�-�eQ+	ǔc�u�L�ٻawL�!]��6��[ D�S�b�^�mkbi�cn���ޖZ�t���L�֬m�U0��&��9#tU��l#(�9����ڼ�攍'�̒��9c2,��S�2��h4����ay��a���DK�$W���	�����.��6r\6�|�pj��ɽ��uS<���m"C�P3ٯHn�P�i~v#�7��-ΰ��9gq�zK��yz�[9K�|l�䂠w��b(���c�|�3��Ց����l��Ra�=@�5���)({���p�
F|�CϚ�f��/�C�W.hqZ	H�Zd�IN�8���G򚖊2�m����
c-N/l���UO�5�s:��r� �_Q��������&����PK   }y�X1�GX6  �  J   org/apache/maven/wrapper/cli/CommandLineParser$BeforeFirstSubCommand.class�VmWE~nXXM1`Q��@$���{��i�P߆e�&���R�G�࿨G=T9j����?C�wv�J�l��fg�ܹw�s������� Ɛ#���Z�l'�{�ܕ��x(�ā#����0�ļ](kg%g����='�َ\�9E7��]�'�M欜;M0�V�YL���)픭J�o6p�f��n�؝$$���P@4b����lk�@8%I���q����T�urg�UZZ�����H䅕M�ө
��ކ�0�h��f�M\"3AӔ�b��$�>8��~�n.�X{�F���u�c���N�j��%\-Gc}��4���E::�C���2���CW��mK�U�̀I�c=����N����#Fh4���#�O�0z0��5H����ʻ���⬎a��g��~AZ��#Sz��c�p��sg	��<�vڞBgoO�wH�(!�2�¸��:�7p�'�}��
�� �����{6sKZa!�4Ih�`EA0���U�XaLaF9�%�����o��/�W��I���>����~a[:��v�%-+�)��ɩqI��׊��W�W7�l\��Z� ]��&�p\A��^����Z�_��ɪ�OY�3b��c�e| L��͗9�(��A�������^�TǶУ�j�U3B	n�_����L&h�)�!�=�ӲeIg>/�Eɬj}E��V������%�%�^	�^�6��-NT��E=1j�|S�๙�=��S2$�u7�"�HD�ܫa	������C��߈�����;�0�m���1��54�u,����X�X��,����bG�#6�̭r���% �М��!"k��?�?�=���<��k�0싯|��#�d�<썱~���2-I��dj�O0v��ߎ0�Q+۵�<�bT��b��	�y
4�.�� M#E3X�YlЂј��I|�[�u�[�^���*��n���h�؋=�'�l�q9�q��R�묫�߇��!�S�i�s�U^8֑��׻큪zw��%�S��g���CqI���Dw�'+�Ϲ��B���s-�W<���ۚz��0Y������PK   }y�X�w5fr  e  T   org/apache/maven/wrapper/cli/CommandLineParser$CaseInsensitiveStringComparator.class�S[OA�f۲�le-E���lA�1%$��I�rIJx�Clg��-�W������g�1��n�P�41��9���?�|��s����v���N�;�s�Xx=~*��^�~_h��J��z\��R�=�C���<
�H��V���PX�k�abS*m1����&M�,�{�O������?�a
�������b2��1��haJ3��WCk��A��`��y�묃9�3؉�a���0m�f���Hv��Y��A����dv�C���a�,�f������c::�!�^���F��~$�n
�P�poPmyt���V��wۼ�twi���F-%�t�Et��;�����eq\R�����ʰ1~.��%;�G���?u~ea����"�\+h_��f�#��L��PJ�z���mxk�Ͱ�MڠU���}O��=�4���v�nIF2S��)�߸aeq�tg�{X$��+I�5E�I*���m-~Df����YS1R�DXS���<k:F-3T���R\���Yԃ��xD8������7T����+�H�˕o�Z�s������'���Z�s\��i��5I+�~ �À��3XA��)���2*�\ų��E�5���M�;�PK   }y�X���  o  K   org/apache/maven/wrapper/cli/CommandLineParser$KnownOptionParserState.class�X[x��ǖ��2	F�MlJ"Z���E���qc;�N�:В��؛Ȼ���N���{��@k��Ih!@�X�q��B(�Jo�ח���/���k{��J�%��_��ٳ�3��s�����^|@;���
e(�mZ�15��G�ب:��qKM�4+O걝��j$ztCۯZ�f��5�qc_��M���wTG(߮��)���)NeG���<�������7�
�]�y��7�)+��A����ݮkh-�A!H�R��Cv��-K�ׂhm)6Hu�h5
��<������'c]����m'�
T2�5��޲�%��ZZ���Lk�@Eϥ�<��G�J\�Hs^Pn^'X�:����yOq1^���Z>(�!�	��_�|EU�$bU7HlD�@��`�r��I�hD���a�٣�]�pzT37��l{��sw�k�� �n�G4'iH��cd�{c$ajv�0����"�Q}-
�(�n���1�7tD�?ڱE�V���߳/�۴FU���'�k�=�
;
G�Ќ���U�hi`7Kt�#d0��Έ�� �( ]FDM9�s����v$e�czBKDa$~����N�R�[�V�F1YHu��&y��+9����(�
�L���JjL����呤}
�I�g����0�+�4�sܲy���g}�n�fw)����"q��MA�L�zqp�#�	��+TA�+�vs����>s)$�N|R��$��a��e�0*9j<��vݦ�V��ۗ�,�K�!�$$4�}%v�4���,SN,�nŖ�+P� �?�9"�sNl���(�
F%��K�J�#��W��!�������yP�K��t��)ws��~g�;G);2�YZ�eLb����Y�cZ�@u�������J|����P⎨v�v��_��"�D�3�C`M}Ca�b�~E�\XWRԳ�c'�#q/��FNإ�,-�2�e�,^�0G�	|C�7%��oQ�<f�K�-��}��!�fl?�qZ����&쎴�LhV��2��h~}@�A��v6ePuo^l�)��v��IV�P����pٙA�X`m�$�I��'*5����)G%�����
�8�Q�\d�ċ�%����Y�*�kz�gg��
��8��nZ��DA4�>�����E}��͏R�_6T'm�����^h���v�	}��K�i�u(I=�3�&TK�w�3�z�_�0�B���^�1ԩ���������]P͉��P�HQ�h�������Z';i���{��vʭ{Y�o]Z����w��5J��r���6n6s������c�ʹ�nՙ�cZ�7Ān�Ь�Iնy���ܵ^�J|��`��?���XԴ},zD+��%�r���HR�ou��e�.U�.�x��<SQ�wnwf	��A�9z{�$M��)(Q1���9TN#<��h�j��S�.Z���њ�)lx����:(�x����J<�Z�֋S� N�Q��s�,e�����w�+1�RWb�Wb�e�"��&���@OQ��O V�͠�!\;���p,�6�F_@M�6Oc�+�l�~���3�U���Q���hk.���'��'��~��4nϠS<�n��jAn�}���%9����H~{}ć�O�sټ.��Ir0Y����X5����s��f?�T�Y��Ϲ����7<��K�o���<(�[;�]9����}E��]�|q/����.�w�A��9��3�����YU{�i�-�#�p8p*�T60�⁙EZ��i��l	�8������������������s������28��2�a�G��I��'��{?}a�|4�3��9~��� �WQ).�N�F̾Hxw�71.��I�6&���Έ?�u�g�CϿ����o�����f��I��уY����Nb�/�O�5(o|{}+��7jo\K ��T���pآ�M�5���i�g���kq�7.GM�W�W:�g�į�,������q�����U�Z\p-��߹��	�NU�Nz^D-��&�F��������=��vo��PK   }y�XmKs�  �  J   org/apache/maven/wrapper/cli/CommandLineParser$MissingOptionArgState.class��[OQ��SJ�[( �xD�Y�"Z��\��	�eSִg����/���A��?��8g���a�4ٝ9��������O �&BGa��,����(ozM�RߵE�n�z�j��V�&�fɔƪ��}e:�)++u״�S���
� D��t���R@b�p<�lv=��Q$T�IB1 �F�\�$Wg�R����Y^��gg�M�ߣ�}������L��zUȊ���,�l�m��⢆K�Lxr�섄%9�^5�3��m�x+1\�p�)K����զ�6������a�^s��ܮ�%��S�,����&�֭��k��ZlTy��d�Eu]ئ��Ű�e:���9��0Ξ����1�;�]Q�O5JHZrْG�i�؏-�Mޛɮv�����"��&�c�0�g�fm�ec�TR��W��R�|U8��D1M��o�?&^O�ەl�(&�v�	�1��|�j���ռ2��s�B�N���\�;�}vC���.t�N�� A.Fym��[�x����χ}h$�?@�K�����Z8�&'�����y����rߠ�B~� �_O�>x��F`��q��b+�v�>��_`\����'�[j�7��f�y�����Q\㛿����}�pZ#��K�O�YPц`����X-�
6�	�,�;�&��S�����.�>y�0V���~� ��PK   }y�XV�{�  �  K   org/apache/maven/wrapper/cli/CommandLineParser$OptionAwareParserState.class�UmOA~��^9��"TŪ i�^)��Ҁ1$�i�߶׵��w���C��/�45���Q�٣`	rD���y晝�����O ��Ћz�5�A�a�v:osc]�-�),}���pt�i�%���V�lZb�;�p��ڞi[[��h*�C�[7�t�!_汨"�^��>hs:żi���ى�a�������Yq$���SNd���w9%V!�k8����ap���or�ɭ�^��j3/T$qA�E�0Đ��������U��ǠTU�pE�U�0|ʡ1��0��g��3AL4\g(ܗ���p�L��*��Y�Ҳ^��$�V����`(���n��*Ƒ�����&d㿧R��Kv�j4.+��p��Z�4�e���*wL)w�a�Wett�*2$���n�I�[��M�-��&h�����4dD��5�Z�����G����0F��2owR�V��K���V�$>��Ĳ�Sjr��)y\���P������%��]��a�Q�sB�H$�C����'�2ERY^H�Od'�������6b���7�{V��(T�F��!G�$�ht�$1B��qo��C��4Gf�(����s��G���r�w�]���Y����"rV!�1r5H���#���5)I�����:��'��Ĕ�XQ�w��q�;4V{�P.C��}�� Y?��a����*����R{L�!�����C PK   }y�X��`�  �  E   org/apache/maven/wrapper/cli/CommandLineParser$OptionComparator.class�TmOA~��zP

UPD����
"�VM�T0�!�oKٔ#�]swE�?�/~#��?�8{WAڪ��fvfw�gf����� ,�E
B*�1Dp�aӲ+����j�X��������UC�[�7��)�p���n�5,��unsײn���n0�R�(�����#~̵*7+����(�Q� ��7�>�3,�4��!Z��;�t��p��V��b���eSrmì\�E	���R��ά.iHⶊ;1�`�a�
jުV�?�P͠ĶR�N~���6���o�R�������>�N@�y��1\�X�33���=�a�j��	��T���nId�=��3�oN�_)=��tAE�a���u@��偝Fm_�o�~�,�E�̫{�6�i��m;hC�>�h�<[�!4���9BD���h�x�}���4��ݒ.P��u@�yM�¦T���\���]2*&w�3��	AK=ւR�A��%�a��+CV(�fAb�LS��*w��0~�{���ɗ�<>T<��%�a��z:]���$�N�ViZi$����Ϥ(���tCa�%=�o��I2� ��?�e�OgO=G�;��_���\�i5|�1��	����e�>�H!��1�D�����ͨR[��J-C��&�E��N�OHǷ	�"�$�e�н蝙�@�}��:�K���`K�u+�Y.���}���
I�I�LW��p	<B����3o�
V=�c��W�ulb�"�c��<�a��<FPK   }y�X7A�ϳ  !  F   org/apache/maven/wrapper/cli/CommandLineParser$OptionParserState.class�SMO�@���$i���~іC�Dm*�T�RH�r�1+��^[��?�kz��C ?��['Bpq�e�=͛��^������(�\���*��Ą�HEp*�X�K��0"M��H��$��>(-��ɤY?L�J�Za%��Iiew	�NwTG��? ��Ĺ�#�C�p|&K���	,8��I<��G�Y$��F�(WϚ�=U��྿�#4���ya�MH��t���t�'��&��|������Ck�{��o��~��N�8_˓i$]��;Ewl��#B�b��]|����	��.�}K��R 4�i-M?Y&�*^6��",޺�*��?S�����͢	xk�zjp_�� p&\���8���?��0���I�h0oLx�&��+@+�E,���s\v��S�g�K3s�ۼ=L�s�O�\��g��(3��^�_3�q�V�PK   }y�XO��4�    A   org/apache/maven/wrapper/cli/CommandLineParser$OptionString.class�T�NA�N/l[���\TT�v�l[�ZP#��A�b�߰ݔ�v��]P�'�/$�&>�E<��"���!��˙���sf��ɏ_ �4��
b*��!�����0�M�.�L[��F�tu�f��N�.�ʊe������Z÷{�w-�J�q�)!��#��^vUo.�S�(�"��[�l�L�ɶol��6RPѧ�_��[ֶvL�'D���%HT��	�|>�a�(��bc��-�)��ｷ�m�PQ�
&#�.<۵j�Ma}���b78B�4�B�w"k3�4eIqK��		�i�̶C�	�(/�S�IH��[^�&����I�-;�d�Vw�[��Vl�LY ���Zrc����V.pʄ�u__�FH��_E��i��W�sA6Eٔ��Y��0Lϛ*.��M�B�w(�ug�5��L�pü�ԗ�m��5�y��`�0ߝ�8�E�B�ۄ��J��!����2�m�>�6�� ��lS��h���f���At.�� �"F1$)�~�A��M��H��`$Ex��9t�s`Q��t����b�'�7�qUN����;��om-�>����h�)�C�@���u�W�~��kA�
�t��Ƹv���itM��3L�&S��b^c�1ɬ��C98�I ך[N���+�$D�p�t�p�MG�ߑ���PG� h�QP�e,a���t�PK   }y�XX����  �  K   org/apache/maven/wrapper/cli/CommandLineParser$OptionStringComparator.class�T�OA�����q����ł-E��SB$��&LjH�m)����k��O��/�hb�տ�g�� M�fvf�7��۟��|��u
"TQ�0<w����\V�	�z��FCxV�f[y�^��A�v�+���m���v*�mp���гa;v��I��t��}�!?�V�;kw�P��04�`���IH5�T�j�T�p�a������o���^�Ò��j��%x6'����8�V��l�x�M�Ӓ.�	�5L0���f`׬˽T���ʬ;����^��Y��n����g�}ۨU�gx�-��'�#Fn���|�_��hR~���co�n�+��n�7��]iK��:-�]�mT����E <b�|XdX�~F}%���)'�q[�Z���w�������-��;�-�CF�q���q����Bwe�m�����5��n��E���`H�Ӡ�N/����"�HF�Ρ}&EA���e��� L�>I&�������ʜAW?BS?A�l�_ޭ��c��	��i�D�ǈ��_a�a��P��)�D�ē�(�(�8��f�q������dX�J+i� �=�ð�-<"M�'g�xbNc�4��7�c<c�`��W7��N�NH:�!��|G�<�3�=�GOb�Ҵ�	/�)�5�n�S�J������[t��C��̇�O�J��a��,LP�,5#�Uy���PK   }y�X����	  2  @   org/apache/maven/wrapper/cli/CommandLineParser$ParserState.class�SMo�@}�8upS��P(P ih\ B�@B��JA9p�$��U���n��ąH��(Ĭ�V���Z�gf�͛7��?��5)�mX9d�Bx�+F�;��/Τr?k1I�v���|_�^�S�X�P�Ĵ"I�ʁ���!]��d�B�D�	w(T�=��n��*c�jUk�jc=�<
S�V�=�'ؾ�XKH�(7g��O����ux�w��(:C^)6�����L<^����d����B}�#9���(�E�zṛ�o���%�j*k���m.�6��ּ��@�>^4�K%P�w�GxY^��Y��&����/�KpZ������9���f�&�>(%uc(�P�6�j���qN4oc����gD(̭��#�/;k�/u����9r����O���I��6����� 6P,�b��l�:g�u���@��M)�)%��&�naĄ�q'&��]^'ܻPW�c~g�mM(��2o4n�_�M�ۚK.N��\���>�ic���D���([*l�l�x>�PK   }y�X�Ć��  g  M   org/apache/maven/wrapper/cli/CommandLineParser$UnknownOptionParserState.class�UISQ�:� !"�"j2,	���R�UR����L��d&5� W��)�<����^�xQ�~3�M�
<��t��M�~�~�y`w���B@EM���M��Hoꉼ���Ķ-
�N�sFb��煙Y6L}U�E�xb>3�m�Q�1,��[s�����p�	�c�OŖH䄙M�9�afg������T9;F�6��Hf�04��9��6V'��X��r��u��6����:k��J*:q�В>�!$eMBu���,!X�"�;��XΩ�E!l��\��u>?�\ᒊ˸Bi,OB��d��8�H�\���uJ.�"��K��R~C����.�������.o�M�HXj4����,�-�;Eq�Ζ��p���m��a;+��� ��=�Y���i�&(����?_ϓ\$�o')�Q9��a�0�83�f��~ߐ<Z�3"�!�K��ۋ9Q,��nW�58����"�����6�Ǫ��n��F�A�o�¯�����u���JnO,�X���y��U�e�Fi�{P��=D��=t��Wy��	���a����3���L� p�w&�;�.}�L�����]��Z����請�z�e\B}e�D�2����_�ߪ��c�e�i�������QO��Q�cmw}�Vb�g�"��v?u�Yt"������Ϫ�"jD���֙A��684| ��+������S�J�H�g��xV� $�r6�1��a�E��$�~wg
�8�i����Na��m	Q~PK   }y�X/���x  �)  4   org/apache/maven/wrapper/cli/CommandLineParser.class�Z{`�Օ��df���B � J�!IHHD@H h�jP�̗d`2g&@,j}�g�֢�>Z����"!�W�V���խ[���v��ڧu��<�w�<2�Bp�����=��s�=�wνɋ�
�l�/Y�6�0�K03n+�:��v����l˷���N;\��׆::��o�?h���;,��ٝa�Ŋ�C��a0�Vn�6[��P�Z�s�J-�6��`�����m	��T���S4��)��/�n�RLGbr���@y�թ%��$����[m_\��1�		MVuE;���d[1�rP�qN01^A��X�����H��d���C����� '�������?Z-��̴^U�U���i� U�5j~2���H��)��y��B5�����NK�p�i�#<8U1�Pt�{P�I�a��r8-!_˨	��n:�eEV�#\hZ������*Nq�"�R�nb6���*ˋR븴�����A�#ɗZU�8Z$a20�g�h.����B�X$�?�y��i���TkC� �f��y�Q��d`wӨݛ���j���N�ޝ)X:�����n����p$�����`9��jg�}�w����nS���hk.V��@��UXM���[Ŧ/ƥy�.>^��s�h�\�aMBJ��h�"��X~�S_6фuGP�M�N�f�'�6�+.4a�YP9L�c�ƨ�uyVw�͏p4Gđ����XU��� ��̃�h5цv��}Q��bId�:U�Ԯ��>�#�~T ����O@��^���߰��ԋL����+JA55х͂�HWs$�Eu�Cݦvj��n\̜��ܞ��?\+b�S��Jl�%.5q�&X�E
F�MQ]���E��3UC7�x�^_8�D9�*W��t�?賷�je���uj�Z���T:�ev�6�`�F|�BCA*��a�b�3������6�VU���}�T+B2�K��$>7��bk���f���h>�NM�|Y8b)܎)�&�0�SeZnK(����
���.��`K�t�>UD��7�b�R���1q/���&m
2��2:�Ҫ���&��%�t�J��M�Ha��6;�Җ�z�ďU�qK��΀��f���Cx�
�`[�]�c�ʛGM<��07l�{/5�c�W�ZNbw�}�����0�V7�Я`䉁�s��I��=�V���6+��g��+g�����ƳJ��n%���������ܐ��XD׳�/�x	/���D��W|N�@���Dp�{��&ۗ��^Qa����o�	����/ˎ˓�K��xփ��ůT��d{>:�L�r��K�K��Ѓ��E��=�n�}�k#�;HF$����X�JU;�&>�HC~��#8�X�J�_&�[%�J��v�%�ז��G
���_�W*ҩ��y����!%�#Wsm�����k�&>��	�5O������ʾYm�b�����fR��uzJ�,�d�25���SD�8n�Fu�nɦۦE�մ���vq���4�)9B�t���pn�ώ��'��-&7m�BI �Q���
u��2�),T**}Ƙ2V���5�i7d��P��V��0���ܐ	D�L!A����Y��qXɮR���j�݆�"��ҧRv�H ��-v,xd�`R,%
�O�L/�G
� Oy��B�����$��4��S����B)"4I�z�5j����F�Q�Cd�?��^k�`0-d��RԵ�n01��Ѭ�[�Z8�l�L�;K=r��2�tSf��3j`�� k0��gG�aۗ���vwq"����k�Mc��}A��lx`WU]�H��lu�ᆮ�f;|n�L��2�b�Xa���:��~�U1܃K��s:q6�p|l���Z7�O&z���d�E��s��0$j����K4�K�x���)A�nS���JT��?! FtyCqM�-%al�33+N�`�u39ۣ�ě�_�3eا�Xwg"�2�3Ļ��ֲ��g]w̼�i�.����a�	��u}ְOڧ�鼈�F�PJ;������h��Fү�T����0!Si���|��aIe��W5�I�8`�U��E^z=�[��ueL�&J^:�@%Tg�T[i�3�����n�Ba� {�@V�I�Lh���f�*ǁ�s����6$��ك�KG�U�����=In��&P����ͺ���w�J�Zu��=��%��E�[N5yL]��ՇL1GJ�"���bG"S�TTN��_1{���r�)95×3��}@�|%07�q�V���?��r�>v��iu�[��~?ㆈ����A;����/Q��`2�#�L��1S��X�2\��>�R�̧/ƨ�kC�%h�j�VĮF�`��o��ʞt
C�s׆�j�_p���)C�3���#�2.�!w������#1a�k�Kd7仂�㹃���LÆ�s����-V�N�R�	C�?�{�Ui��L��)<�O��sFBd��d.�Gã�՟���Wn�ƫk=~�V��g�k!�F��#����ń^���6��� 8�lu2�of�\*��O%�0.�|�R�Kzq
ŝ�ԋ����kqӌ^����Oj`aV�D�k��b2����,�:�c�,$oL�,M�S2cJ{0롤X�Vom�������ETv1�i�Ԑv6� [	�~�~ʥG?�6�c^S�c��C���{Q[ߏ�M%�ҋ�f�a�}��_쐗��Z�3X_�,�:{p��q~�z�w ��&ź�a���u�{йH�u�$҃-�.��_%�6\�	fz�y{p�\W����(Z�kx��P�^O�3�t�f:E�:�qR��:]I�I���( ���މ�~l�F��4YHT�Rf{]}�Nv`��劯���/�+Mi�)//��tg�w{p�"���W�-�n��?؁|�;a.U��j���ܜ����<Z�/e�5��t���HOR��W�Ȟ�?ۃE��HU7>�ӎ��"��-{�aϡ���-U����ԃ2�63�[0>L����l��\�Â�����܈lG��c��Oq^C�A�!����YF`����r��K1�1
/�F\*�q�l��҉+�\)��*�W��ދ��	� ��Fy7ˇ�E����1n����pGVv���H����R��vL�e��o���j̠ffl����X�1YΔ��񸹜yӈf=��9�b� g�Π1^7-�HVp̅y\V�ͩ2*��|��zf�x��4�*��y_V˗���$�ES52?�����sl0p��1��p����oQ�_�P���ޏ,�8%k?\|��8:���5�6Xs��B�ı� 8 "1̻��ߓ�yF�r2�0���M�e!_�1���-⎼֏}
&'�<I z��x��^���,�D}x>k��
��JGY^#1�c0��՛���+]11o*1�B13��Jy�B~�0bRr�9q)k�[�<*�t܍�^w�l��;���[�N������>���P��Tb�#x(�;a���7���ß�}E�\��Կ)��v.IZ���[\;���2��}�x�s��w���,�$�A�>��z�c$��$&7esF��1hQUiܠ1�Kc
�=2b����r�149�Bc���+����r1N��OX����`�j"���D���m؇���S���I�����Qd?��!�\<x����1�f����/I^�-xEnƫ�^�����M{7�x߆�Ҥ��+�P�c�y��/0o?�T����u�|Eӭ�����۳�g�Lo�%�4����jy��ܢ��y����J��r�l�b������|B4q�L��̈iҞ�������c1�2$@
���è��@�!�x�@�ğe��0�~W�Bb�~�H�8��?�g���A��A�	��c����Sm��~�A^���!ޒ|�ovY8�_
�z�zG������peI��O�0-�+T:Oډ��=r��R�9Y�eW�9��g�@�����>��5ME�&���R��
V��x��[,��h����+��b��XE�J/�Hº:["�K���D�ҥ�X,������~�Vy|<1ڐ9 C;�N��)F�Qu+k�
��,��)O�Q_�Tu�\G��{q^i�cV�S��x�0�_
����x�9���^��N�#%I���W9X�����M�����R�k����L�%Q¡Wu�&��=]߳�og�^�������+��#���h䎮�����|���)ng��gI���r]휜�3D������.�X���Й���9�Cp�U�;+t$~�A�0e�M�s�6V�X�)��#�w���V��\�&�e�̹����̗g\�8����+30�1��ܿ*y*��g���Y=R�
��G�����!��Ky2'���_�k4��r~]�|�N	�sc�op���ɒ1����C�i;Kn���|~�σ���]9r�o��6���y;�lce'���}'�w��n>�ud���܍	�PK   }y�X��sP  �  4   org/apache/maven/wrapper/cli/ParsedCommandLine.class�Wiw�~��-��-�`'۲l��M�LL�b"��v�2X�<D)����I�n�o$�7����(�t�֞��~�hz8�Ͻ3�%{�Ѽ��{��y����>��_ ��w1���E�@�ec�H;�>{�9�
4�l����hh�TJ�*�O�I���pZ�kh�юmCE'�6J����^4�L;}�1J%�I����S6s'������Z�)��Xr��]>�<�:���Nv���j���}�tc���б]�%�,��{�3�h�hh�����q�1��V��vy���(�)���N��H�4�QgJ�m�^$4���8����pMǸX`j͖ݢ#���_ga�ߧ��8��WG��VT��<�xT)��:R��j{G�v]�k���QW2�y�z'cF8�!��x?���͵:t6$/+ъ�%zG�^hm`�G�V<�1�t| �GH�R����6��q' �f���0�8XN%�/�b$�I��A��J�5��ܙ�e��;������hL�8���^��΢����mT���q�Z&v��>^�
9Ӊ!�p3��rqN�4^�cYK;'0�x��A���ҋ:�p��v�Վmd`-�V���#�(�z�
�	��)}\����e�h��%ɶ3��mr�pl�k�9Q,�Z�y�Z�og�pw�{^��e,�|$��� �AMYx��-�7
U���:o*��xE`�D��`�7a�Dμ��sC1�(���n�L+�pE�U�����Š�\N��	�ī���x� �ݗ��9�i+on�!�s�#��� �V�1����Y`[jX7�7Q�ܜ$s�D1'??��Te���M�x�8ofǒk��#0-K��8J*�*q��Z���~0�H�]���yr�K�Ԋ��#&~��X.U�8��aC�j�v����!��DP��`Y�����'��rU\����t�Q�*���(��(�mG�o݀yIq���Um+�Ys�,�>�]��)�|�I���M'��;��>o�������77����FY�1
=��=[�_��Z�d{Xl�����t��̛�,IɮzC22��G#��tqy���k\��)�y!��?�5o����h�3��P�, FhA��k�;�{����b;(�oa;�]�o�g�.�L����u<G!)�7M��0ug"����]<%��vG}����b&Ӕ�b�Mݑ�8����VV�6#��s�b^@/W���~���y��c��i$aa Wx��A�J�ϫ�Q��xM�:�/�K�p��|�2?|_��5|�x|��Χ��MJߢ| �U����V{5��yK�0��p�D����<���$�ѷ�������p��n��g��"R��q��:+smW�F�`��\��~�!y��*�w�l�~�!K��&#c^92	�zDJ>����D,?{�M�?��ѺUxle��ۘ�������sG�{�`�2�g(�a����E��I����?"�!�Q�C�s��B���������2�5��u��?@\CV���Ҕy�>�oR��$�	���L�ƅY�5��X7j�ƔB��M�H����:Ɏ7hN�-r�C�->#|���|�{�T�L%I�K���XY�w�F�.��,)�"�#�.��/)�	��À�CY��Z��~�@G�����ƣk��)��b����,OɾbЗCrx2�w�{.^�(2H�݁���M55d�q��%�0Oy��^����S;Q�R�5C�G/ۂo~�������0��kT�Zî�x�7�������ӳ~mui�9UB�}{]YR�\WV���_�p�_�7�E�/���$kRj`]����������U�'�Y��٣�P�3!jw(���]Q�~�?PK   }y�X�v�E�  a  :   org/apache/maven/wrapper/cli/ParsedCommandLineOption.class�TKOQ=��-��Uax�m��(�G������IM�.��N������čLT�$��e���@���E�wN���=������y<������ ��[|��7+���V��tS�s�D�(!����l�P�,������ݐ%�ȸ�^���UQy����Z�ۚ���x��Yj���W�jj��z���r^7�����M��FKk��|[Hhd#��'#�~�P�7�"�1�!a�a��~�0�
7
6����%���2�:J��5�n+U��psGqt��h��`"ߦ,ؖnV��b7FpM¨�1(�κd4�]��jÞ"9Vj-����5ӂkJ�uLSBE���j2�9���ę�fDvJ�nR6/�;{�������Izs�ְw��br�e�٩�^/�/<��Җ�e���z٪mj�k�i��K�(rK���:�1���E �5ě�@"y�C���ox��������a�xA����%�Ҫi&%�O�����-��=Ӆ��*J�D�������&���|���c���6!9��DS`�#H�E�}���g��<�d7w�q���ǵ�qeR��C��c�\�����!�iV<x��b҇_��O�~G1N�@[/AA&0�I$hy��O�����	}�,9>TzQA��RH�&O�=�5�=W�SBD� �9D҇7�M��&�����S����{�bn7f� >"��tR��q��ةR��Eɢcd�/PK   }y�Xx��͊  5  H   org/apache/maven/wrapper/cli/ProjectPropertiesCommandLineConverter.class���N1��
"��n\�&0w^Ÿ"J�q_��P23����te���-�18Q�8�����M_ߞ_ b'���H0�CM*��1�:�|@�{�x�r�@�g��V��%���j2yԮ��j2�C�XDB�2�J��r��C��+�%OWL�s(0�7I;�f[w��I��9���N�S"�N��~թ4¡�����T�f�h���xc|�o}�k�MKV��-R7��X�n�v=Ӻ#z��}İ�Ӈixk!#��R����<�ݦV"�u����4�v���7��oʾ��R������ {X0?Ŏ4��+&.��k23y��	��Lf�lbf$na���؀"VG��'pe��ه)t���D7йit/�L@�����kX��;PK   }y�Xkn�4�  &  G   org/apache/maven/wrapper/cli/SystemPropertiesCommandLineConverter.class��AO�0��nK��Bi�1��2���\&q &�V;�2&e��M��$��(�j��v�؇�p�2M%�I������=�m����� |�Q+(հ�2C_*��1���|N�s�x�r�@8�D+��JIJ�2y4���2��C�LDBb(�;#X�2l&i�)�Ʀ��ÉK�^�v������S�/GWv�z~���4�=�9f��U��p�Yw��%����C=����1�o|�9�F\�,_�%=�y��O>eh�����u����ڝ�w>�N�#�q��oJ��HsЄ�8g/�Xu��r�<�,2��������-�5�Q�>��u�9fff��pvg�X^���0��,@�|�+�����ʏ5����}���YG[��A�����A��FVu� PK   }y�X��c  H
  =   META-INF/maven/org.apache.maven.wrapper/maven-wrapper/pom.xml�VMs�6��WluJfL�9t\�%��jl�c�qs����X 4��HJ��8�N}����ݷowIE�e/�4��jt�� E*3.������u�1��_|߻�)
�	f�0�XJ��4S��3ލ���@GT zRA)	�Ja_ԆEX�KF 	��=��'�o`��2�['J�p�" ��H�K�ò�ۤ� .�P:
�)[%�6��+�T�W�in+Hn{���R}Yw��v���V/8N��Qw5z��s-��4Pk��\�XbI�ʪ�L��um��o]�0���U r9�3��2��æi�hR�a_UxG2N���zޣ(PkP�w���� ��H�D�`X=m?\�)w�H[��x���;�zVT�@1��	L�|'���&��g�sx?<����M��<�^O�ٔN�0�~�L��'��%�u�,w"ȭv����n���CW��%O�$��,G�%͸��P�*�����-���ܸ��ޫz��i�+%�cj�vE�Q'y�^PE�sv~N�9��Z�-�6��p���ӳ����K�s�����R;�L���M�>Y�5��y��D�̰�8vwQ�g��U�nab7eQ�d]M����)�|��B�=��3e����-��Q�p h=��P|\�Q�-����y?�E
Vb|o1��b�F8g��u�xe�~����d�	�E70�T��vW���@b;��	�4wc�w?A��v�A�x-�
iE�Qw�lM��pД+S�sЈ}�v�C�8?��]jzal�o��e�����>+n^M�>Y��;��*~���F���37�MJ�O�e�����3�U�Q����[G%�[��̳��K_a�L?�㵨y�=Y��i~w��mh:�mLk,��o��T�\q����1�:�iѲV鶐m�aE�Q��y���V�g^�� �V�=�1'�̙(6csӓ}����?�=��t�_��rM�ݔ~�l� �tGn^�Ya�@���~���N��-,
C��Y���c��PK   }y�X�\�@   H   D   META-INF/maven/org.apache.maven.wrapper/maven-wrapper/pom.propertiesK,*�LKL.�L��M,K��-/J,(H-�J/�/- ����%$&g������g�����q PK
    }y�X            	          �A    META-INF/PK   }y�X�1Oe�   J             ��'   META-INF/MANIFEST.MFPK
    }y�X                      �A�   org/PK
    }y�X                      �A!  org/apache/PK
    }y�X                      �AJ  org/apache/maven/PK
    }y�X                      �Ay  org/apache/maven/wrapper/PK
    }y�X                      �A�  org/apache/maven/wrapper/cli/PK
    }y�X                      �A�  META-INF/maven/PK
    }y�X            (          �A  META-INF/maven/org.apache.maven.wrapper/PK
    }y�X            6          �A^  META-INF/maven/org.apache.maven.wrapper/maven-wrapper/PK   }y�X�G�q                ���  META-INF/DEPENDENCIESPK   }y�X���m  ^,             ��V  META-INF/LICENSEPK   }y�X��w��   �              ���  META-INF/NOTICEPK   }y�X�۱A�  U  3           ���  org/apache/maven/wrapper/BootstrapMainStarter.classPK   }y�X܇�H  C  2           ���  org/apache/maven/wrapper/DefaultDownloader$1.classPK   }y�X�4'0  �  S           ��s  org/apache/maven/wrapper/DefaultDownloader$SystemPropertiesProxyAuthenticator.classPK   }y�X�3�    0           ��   org/apache/maven/wrapper/DefaultDownloader.classPK   }y�X�y]�   �   )           �� .  org/apache/maven/wrapper/Downloader.classPK   }y�XK>8ڤ  {
  4           ��/  org/apache/maven/wrapper/HashAlgorithmVerifier.classPK   }y�XXW1�  *  *           ��5  org/apache/maven/wrapper/Installer$1.classPK   }y�X[/A�  �#  (           ��r8  org/apache/maven/wrapper/Installer.classPK   }y�X;n4GR  %  %           ���I  org/apache/maven/wrapper/Logger.classPK   }y�Xb`3�N  ,  /           ��L  org/apache/maven/wrapper/MavenWrapperMain.classPK   }y�X���|�    >           ���X  org/apache/maven/wrapper/PathAssembler$LocalDistribution.classPK   }y�X\�@j#  �  ,           ���Z  org/apache/maven/wrapper/PathAssembler.classPK   }y�XR(��  c  6           ��#a  org/apache/maven/wrapper/SystemPropertiesHandler.classPK   }y�X��   a  '           ���g  org/apache/maven/wrapper/Verifier.classPK   }y�X�W!  �
  3           ���h  org/apache/maven/wrapper/WrapperConfiguration.classPK   }y�X��e��    .           ��5m  org/apache/maven/wrapper/WrapperExecutor.classPK   }y�X��   T  ?           ��Rz  org/apache/maven/wrapper/cli/AbstractCommandLineConverter.classPK   }y�Xm�v��  -  I           ���}  org/apache/maven/wrapper/cli/AbstractPropertiesCommandLineConverter.classPK   }y�X�:�dP  g  ?           ���  org/apache/maven/wrapper/cli/CommandLineArgumentException.classPK   }y�Xlk�I  �  7           ����  org/apache/maven/wrapper/cli/CommandLineConverter.classPK   }y�X��I�U  �  4           ��=�  org/apache/maven/wrapper/cli/CommandLineOption.classPK   }y�X�#�ر     6           ���  org/apache/maven/wrapper/cli/CommandLineParser$1.classPK   }y�X�@Ƀ  �  I           ���  org/apache/maven/wrapper/cli/CommandLineParser$AfterFirstSubCommand.classPK   }y�X�wM��  0  A           ��Ӑ  org/apache/maven/wrapper/cli/CommandLineParser$AfterOptions.classPK   }y�X1�GX6  �  J           ���  org/apache/maven/wrapper/cli/CommandLineParser$BeforeFirstSubCommand.classPK   }y�X�w5fr  e  T           ����  org/apache/maven/wrapper/cli/CommandLineParser$CaseInsensitiveStringComparator.classPK   }y�X���  o  K           ��s�  org/apache/maven/wrapper/cli/CommandLineParser$KnownOptionParserState.classPK   }y�XmKs�  �  J           ��̣  org/apache/maven/wrapper/cli/CommandLineParser$MissingOptionArgState.classPK   }y�XV�{�  �  K           ���  org/apache/maven/wrapper/cli/CommandLineParser$OptionAwareParserState.classPK   }y�X��`�  �  E           ��=�  org/apache/maven/wrapper/cli/CommandLineParser$OptionComparator.classPK   }y�X7A�ϳ  !  F           ����  org/apache/maven/wrapper/cli/CommandLineParser$OptionParserState.classPK   }y�XO��4�    A           ����  org/apache/maven/wrapper/cli/CommandLineParser$OptionString.classPK   }y�XX����  �  K           ����  org/apache/maven/wrapper/cli/CommandLineParser$OptionStringComparator.classPK   }y�X����	  2  @           ���  org/apache/maven/wrapper/cli/CommandLineParser$ParserState.classPK   }y�X�Ć��  g  M           ��O�  org/apache/maven/wrapper/cli/CommandLineParser$UnknownOptionParserState.classPK   }y�X/���x  �)  4           ����  org/apache/maven/wrapper/cli/CommandLineParser.classPK   }y�X��sP  �  4           ��{�  org/apache/maven/wrapper/cli/ParsedCommandLine.classPK   }y�X�v�E�  a  :           ���  org/apache/maven/wrapper/cli/ParsedCommandLineOption.classPK   }y�Xx��͊  5  H           ��c�  org/apache/maven/wrapper/cli/ProjectPropertiesCommandLineConverter.classPK   }y�Xkn�4�  &  G           ��S�  org/apache/maven/wrapper/cli/SystemPropertiesCommandLineConverter.classPK   }y�X��c  H
  =           ��F�  META-INF/maven/org.apache.maven.wrapper/maven-wrapper/pom.xmlPK   }y�X�\�@   H   D           ����  META-INF/maven/org.apache.maven.wrapper/maven-wrapper/pom.propertiesPK    7 7 �  O�    
```

## media-service\.mvn\wrapper\maven-wrapper.properties

```bash
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip
wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar

```

## media-service\src\main\java\tg\ngstars\media\MediaServiceApplication.java

```java
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

```

## media-service\src\main\java\tg\ngstars\media\config\GlobalExceptionHandler.java

```java
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
                fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalide",
                (a, b) -> a + "; " + b
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

```

## media-service\src\main\java\tg\ngstars\media\config\MediaProperties.java

```java
package tg.ngstars.media.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "media")
public record MediaProperties(
    String uploadDir
) {}

```

## media-service\src\main\java\tg\ngstars\media\config\SecurityConfig.java

```java
package tg.ngstars.media.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import tg.ngstars.common.security.RealmRoleConverter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
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
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new RealmRoleConverter());
        return converter;
    }
}

```

## media-service\src\main\java\tg\ngstars\media\controller\FileController.java

```java
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

```

## media-service\src\main\java\tg\ngstars\media\service\FileService.java

```java
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

    private static final java.util.Set<String> ALLOWED_EXTENSIONS = java.util.Set.of(
        ".jpg", ".jpeg", ".png", ".gif", ".pdf", ".webp");

    public String store(MultipartFile file, String userId) {
        var originalName = file.getOriginalFilename();
        var ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf('.')).toLowerCase();
        }
        if (!ALLOWED_EXTENSIONS.contains(ext))
            throw new IllegalArgumentException("File type not allowed: " + ext);
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
        if (owner == null)
            throw new SecurityException("File not tracked: " + filename);
        if (!owner.equals(userId))
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
        var tmp = ownershipFile.resolveSibling(ownershipFile.getFileName() + ".tmp");
        try (var writer = Files.newBufferedWriter(tmp)) {
            objectMapper.writeValue(writer, fileOwners);
            Files.move(tmp, ownershipFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            log.error("Failed to persist file ownership — in-memory state may diverge from disk", e);
        }
    }
}

```

## media-service\src\main\resources\application-dev.yml

```yaml
springdoc:
  swagger-ui:
    enabled: true

logging:
  level:
    tg.ngstars: DEBUG

```

## media-service\src\main\resources\application-prod.yml

```yaml
springdoc:
  swagger-ui:
    enabled: false
  api-docs:
    enabled: false
logging:
  level:
    tg.ngstars: WARN

```

## media-service\src\main\resources\application.yml

```yaml
server:
  port: 8084
  shutdown: graceful
  forward-headers-strategy: native

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
    enabled: false
    path: /swagger-ui.html

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    env:
      keys-to-sanitize: password,secret,token

```

---

########### notification-service ###########

## notification-service\.env.template

```text
SMTP_HOST=localhost
SMTP_PORT=1025
SMTP_USER=
SMTP_PASSWORD=
KEYCLOAK_ISSUER_URI=http://localhost:8088/realms/ng-fields

```

## notification-service\.gitattributes

```text
/mvnw text eol=lf
*.cmd text eol=crlf

```

## notification-service\.gitignore

```text
HELP.md
target/
.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

### STS ###
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache

### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr

### NetBeans ###
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/
build/
!**/src/main/**/build/
!**/src/test/**/build/

### VS Code ###
.vscode/

```

## notification-service\HELP.md

```markdown
# Read Me First
The following was discovered as part of building this project:

* The original package name 'tg.ngstars.notification-service' is invalid and this project uses 'tg.ngstars.notification_service' instead.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.1.0/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.1.0/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/4.1.0/reference/web/servlet.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/4.1.0/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [OAuth2 Resource Server](https://docs.spring.io/spring-boot/4.1.0/reference/web/spring-security.html#web.security.oauth2.server)
* [Java Mail Sender](https://docs.spring.io/spring-boot/4.1.0/reference/io/email.html)
* [Thymeleaf](https://docs.spring.io/spring-boot/4.1.0/reference/web/servlet.html#web.servlet.spring-mvc.template-engines)
* [Validation](https://docs.spring.io/spring-boot/4.1.0/reference/io/validation.html)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/4.1.0/reference/actuator/index.html)
* [Flyway Migration](https://docs.spring.io/spring-boot/4.1.0/how-to/data-initialization.html#howto.data-initialization.migration-tool.flyway)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Handling Form Submission](https://spring.io/guides/gs/handling-form-submission/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.


```

## notification-service\mvnw

```text
#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.4
#
# Optional ENV vars
# -----------------
#   JAVA_HOME - location of a JDK home dir, required when download maven via java source
#   MVNW_REPOURL - repo url base for downloading maven distribution
#   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
#   MVNW_VERBOSE - true: enable verbose log; debug: trace the mvnw script; others: silence the output
# ----------------------------------------------------------------------------

set -euf
[ "${MVNW_VERBOSE-}" != debug ] || set -x

# OS specific support.
native_path() { printf %s\\n "$1"; }
case "$(uname)" in
CYGWIN* | MINGW*)
  [ -z "${JAVA_HOME-}" ] || JAVA_HOME="$(cygpath --unix "$JAVA_HOME")"
  native_path() { cygpath --path --windows "$1"; }
  ;;
esac

# set JAVACMD and JAVACCMD
set_java_home() {
  # For Cygwin and MinGW, ensure paths are in Unix format before anything is touched
  if [ -n "${JAVA_HOME-}" ]; then
    if [ -x "$JAVA_HOME/jre/sh/java" ]; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
      JAVACCMD="$JAVA_HOME/jre/sh/javac"
    else
      JAVACMD="$JAVA_HOME/bin/java"
      JAVACCMD="$JAVA_HOME/bin/javac"

      if [ ! -x "$JAVACMD" ] || [ ! -x "$JAVACCMD" ]; then
        echo "The JAVA_HOME environment variable is not defined correctly, so mvnw cannot run." >&2
        echo "JAVA_HOME is set to \"$JAVA_HOME\", but \"\$JAVA_HOME/bin/java\" or \"\$JAVA_HOME/bin/javac\" does not exist." >&2
        return 1
      fi
    fi
  else
    JAVACMD="$(
      'set' +e
      'unset' -f command 2>/dev/null
      'command' -v java
    )" || :
    JAVACCMD="$(
      'set' +e
      'unset' -f command 2>/dev/null
      'command' -v javac
    )" || :

    if [ ! -x "${JAVACMD-}" ] || [ ! -x "${JAVACCMD-}" ]; then
      echo "The java/javac command does not exist in PATH nor is JAVA_HOME set, so mvnw cannot run." >&2
      return 1
    fi
  fi
}

# hash string like Java String::hashCode
hash_string() {
  str="${1:-}" h=0
  while [ -n "$str" ]; do
    char="${str%"${str#?}"}"
    h=$(((h * 31 + $(LC_CTYPE=C printf %d "'$char")) % 4294967296))
    str="${str#?}"
  done
  printf %x\\n $h
}

verbose() { :; }
[ "${MVNW_VERBOSE-}" != true ] || verbose() { printf %s\\n "${1-}"; }

die() {
  printf %s\\n "$1" >&2
  exit 1
}

trim() {
  # MWRAPPER-139:
  #   Trims trailing and leading whitespace, carriage returns, tabs, and linefeeds.
  #   Needed for removing poorly interpreted newline sequences when running in more
  #   exotic environments such as mingw bash on Windows.
  printf "%s" "${1}" | tr -d '[:space:]'
}

scriptDir="$(dirname "$0")"
scriptName="$(basename "$0")"

# parse distributionUrl and optional distributionSha256Sum, requires .mvn/wrapper/maven-wrapper.properties
while IFS="=" read -r key value; do
  case "${key-}" in
  distributionUrl) distributionUrl=$(trim "${value-}") ;;
  distributionSha256Sum) distributionSha256Sum=$(trim "${value-}") ;;
  esac
done <"$scriptDir/.mvn/wrapper/maven-wrapper.properties"
[ -n "${distributionUrl-}" ] || die "cannot read distributionUrl property in $scriptDir/.mvn/wrapper/maven-wrapper.properties"

case "${distributionUrl##*/}" in
maven-mvnd-*bin.*)
  MVN_CMD=mvnd.sh _MVNW_REPO_PATTERN=/maven/mvnd/
  case "${PROCESSOR_ARCHITECTURE-}${PROCESSOR_ARCHITEW6432-}:$(uname -a)" in
  *AMD64:CYGWIN* | *AMD64:MINGW*) distributionPlatform=windows-amd64 ;;
  :Darwin*x86_64) distributionPlatform=darwin-amd64 ;;
  :Darwin*arm64) distributionPlatform=darwin-aarch64 ;;
  :Linux*x86_64*) distributionPlatform=linux-amd64 ;;
  *)
    echo "Cannot detect native platform for mvnd on $(uname)-$(uname -m), use pure java version" >&2
    distributionPlatform=linux-amd64
    ;;
  esac
  distributionUrl="${distributionUrl%-bin.*}-$distributionPlatform.zip"
  ;;
maven-mvnd-*) MVN_CMD=mvnd.sh _MVNW_REPO_PATTERN=/maven/mvnd/ ;;
*) MVN_CMD="mvn${scriptName#mvnw}" _MVNW_REPO_PATTERN=/org/apache/maven/ ;;
esac

# apply MVNW_REPOURL and calculate MAVEN_HOME
# maven home pattern: ~/.m2/wrapper/dists/{apache-maven-<version>,maven-mvnd-<version>-<platform>}/<hash>
[ -z "${MVNW_REPOURL-}" ] || distributionUrl="$MVNW_REPOURL$_MVNW_REPO_PATTERN${distributionUrl#*"$_MVNW_REPO_PATTERN"}"
distributionUrlName="${distributionUrl##*/}"
distributionUrlNameMain="${distributionUrlName%.*}"
distributionUrlNameMain="${distributionUrlNameMain%-bin}"
MAVEN_USER_HOME="${MAVEN_USER_HOME:-${HOME}/.m2}"
MAVEN_HOME="${MAVEN_USER_HOME}/wrapper/dists/${distributionUrlNameMain-}/$(hash_string "$distributionUrl")"

exec_maven() {
  unset MVNW_VERBOSE MVNW_USERNAME MVNW_PASSWORD MVNW_REPOURL || :
  exec "$MAVEN_HOME/bin/$MVN_CMD" "$@" || die "cannot exec $MAVEN_HOME/bin/$MVN_CMD"
}

if [ -d "$MAVEN_HOME" ]; then
  verbose "found existing MAVEN_HOME at $MAVEN_HOME"
  exec_maven "$@"
fi

case "${distributionUrl-}" in
*?-bin.zip | *?maven-mvnd-?*-?*.zip) ;;
*) die "distributionUrl is not valid, must match *-bin.zip or maven-mvnd-*.zip, but found '${distributionUrl-}'" ;;
esac

# prepare tmp dir
if TMP_DOWNLOAD_DIR="$(mktemp -d)" && [ -d "$TMP_DOWNLOAD_DIR" ]; then
  clean() { rm -rf -- "$TMP_DOWNLOAD_DIR"; }
  trap clean HUP INT TERM EXIT
else
  die "cannot create temp dir"
fi

mkdir -p -- "${MAVEN_HOME%/*}"

# Download and Install Apache Maven
verbose "Couldn't find MAVEN_HOME, downloading and installing it ..."
verbose "Downloading from: $distributionUrl"
verbose "Downloading to: $TMP_DOWNLOAD_DIR/$distributionUrlName"

# select .zip or .tar.gz
if ! command -v unzip >/dev/null; then
  distributionUrl="${distributionUrl%.zip}.tar.gz"
  distributionUrlName="${distributionUrl##*/}"
fi

# verbose opt
__MVNW_QUIET_WGET=--quiet __MVNW_QUIET_CURL=--silent __MVNW_QUIET_UNZIP=-q __MVNW_QUIET_TAR=''
[ "${MVNW_VERBOSE-}" != true ] || __MVNW_QUIET_WGET='' __MVNW_QUIET_CURL='' __MVNW_QUIET_UNZIP='' __MVNW_QUIET_TAR=v

# normalize http auth
case "${MVNW_PASSWORD:+has-password}" in
'') MVNW_USERNAME='' MVNW_PASSWORD='' ;;
has-password) [ -n "${MVNW_USERNAME-}" ] || MVNW_USERNAME='' MVNW_PASSWORD='' ;;
esac

if [ -z "${MVNW_USERNAME-}" ] && command -v wget >/dev/null; then
  verbose "Found wget ... using wget"
  wget ${__MVNW_QUIET_WGET:+"$__MVNW_QUIET_WGET"} "$distributionUrl" -O "$TMP_DOWNLOAD_DIR/$distributionUrlName" || die "wget: Failed to fetch $distributionUrl"
elif [ -z "${MVNW_USERNAME-}" ] && command -v curl >/dev/null; then
  verbose "Found curl ... using curl"
  curl ${__MVNW_QUIET_CURL:+"$__MVNW_QUIET_CURL"} -f -L -o "$TMP_DOWNLOAD_DIR/$distributionUrlName" "$distributionUrl" || die "curl: Failed to fetch $distributionUrl"
elif set_java_home; then
  verbose "Falling back to use Java to download"
  javaSource="$TMP_DOWNLOAD_DIR/Downloader.java"
  targetZip="$TMP_DOWNLOAD_DIR/$distributionUrlName"
  cat >"$javaSource" <<-END
	public class Downloader extends java.net.Authenticator
	{
	  protected java.net.PasswordAuthentication getPasswordAuthentication()
	  {
	    return new java.net.PasswordAuthentication( System.getenv( "MVNW_USERNAME" ), System.getenv( "MVNW_PASSWORD" ).toCharArray() );
	  }
	  public static void main( String[] args ) throws Exception
	  {
	    setDefault( new Downloader() );
	    java.nio.file.Files.copy( java.net.URI.create( args[0] ).toURL().openStream(), java.nio.file.Paths.get( args[1] ).toAbsolutePath().normalize() );
	  }
	}
	END
  # For Cygwin/MinGW, switch paths to Windows format before running javac and java
  verbose " - Compiling Downloader.java ..."
  "$(native_path "$JAVACCMD")" "$(native_path "$javaSource")" || die "Failed to compile Downloader.java"
  verbose " - Running Downloader.java ..."
  "$(native_path "$JAVACMD")" -cp "$(native_path "$TMP_DOWNLOAD_DIR")" Downloader "$distributionUrl" "$(native_path "$targetZip")"
fi

# If specified, validate the SHA-256 sum of the Maven distribution zip file
if [ -n "${distributionSha256Sum-}" ]; then
  distributionSha256Result=false
  if [ "$MVN_CMD" = mvnd.sh ]; then
    echo "Checksum validation is not supported for maven-mvnd." >&2
    echo "Please disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties." >&2
    exit 1
  elif command -v sha256sum >/dev/null; then
    if echo "$distributionSha256Sum  $TMP_DOWNLOAD_DIR/$distributionUrlName" | sha256sum -c - >/dev/null 2>&1; then
      distributionSha256Result=true
    fi
  elif command -v shasum >/dev/null; then
    if echo "$distributionSha256Sum  $TMP_DOWNLOAD_DIR/$distributionUrlName" | shasum -a 256 -c >/dev/null 2>&1; then
      distributionSha256Result=true
    fi
  else
    echo "Checksum validation was requested but neither 'sha256sum' or 'shasum' are available." >&2
    echo "Please install either command, or disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties." >&2
    exit 1
  fi
  if [ $distributionSha256Result = false ]; then
    echo "Error: Failed to validate Maven distribution SHA-256, your Maven distribution might be compromised." >&2
    echo "If you updated your Maven version, you need to update the specified distributionSha256Sum property." >&2
    exit 1
  fi
fi

# unzip and move
if command -v unzip >/dev/null; then
  unzip ${__MVNW_QUIET_UNZIP:+"$__MVNW_QUIET_UNZIP"} "$TMP_DOWNLOAD_DIR/$distributionUrlName" -d "$TMP_DOWNLOAD_DIR" || die "failed to unzip"
else
  tar xzf${__MVNW_QUIET_TAR:+"$__MVNW_QUIET_TAR"} "$TMP_DOWNLOAD_DIR/$distributionUrlName" -C "$TMP_DOWNLOAD_DIR" || die "failed to untar"
fi

# Find the actual extracted directory name (handles snapshots where filename != directory name)
actualDistributionDir=""

# First try the expected directory name (for regular distributions)
if [ -d "$TMP_DOWNLOAD_DIR/$distributionUrlNameMain" ]; then
  if [ -f "$TMP_DOWNLOAD_DIR/$distributionUrlNameMain/bin/$MVN_CMD" ]; then
    actualDistributionDir="$distributionUrlNameMain"
  fi
fi

# If not found, search for any directory with the Maven executable (for snapshots)
if [ -z "$actualDistributionDir" ]; then
  # enable globbing to iterate over items
  set +f
  for dir in "$TMP_DOWNLOAD_DIR"/*; do
    if [ -d "$dir" ]; then
      if [ -f "$dir/bin/$MVN_CMD" ]; then
        actualDistributionDir="$(basename "$dir")"
        break
      fi
    fi
  done
  set -f
fi

if [ -z "$actualDistributionDir" ]; then
  verbose "Contents of $TMP_DOWNLOAD_DIR:"
  verbose "$(ls -la "$TMP_DOWNLOAD_DIR")"
  die "Could not find Maven distribution directory in extracted archive"
fi

verbose "Found extracted Maven distribution directory: $actualDistributionDir"
printf %s\\n "$distributionUrl" >"$TMP_DOWNLOAD_DIR/$actualDistributionDir/mvnw.url"
mv -- "$TMP_DOWNLOAD_DIR/$actualDistributionDir" "$MAVEN_HOME" || [ -d "$MAVEN_HOME" ] || die "fail to move MAVEN_HOME"

clean || :
exec_maven "$@"

```

## notification-service\mvnw.cmd

```text
<# : batch portion
@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.4
@REM
@REM Optional ENV vars
@REM   MVNW_REPOURL - repo url base for downloading maven distribution
@REM   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
@REM   MVNW_VERBOSE - true: enable verbose log; others: silence the output
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
@SET PSModulePath=
@FOR /F "usebackq tokens=1* delims==" %%A IN (`powershell -noprofile "& {$scriptDir='%~dp0'; $script='%__MVNW_ARG0_NAME__%'; icm -ScriptBlock ([Scriptblock]::Create((Get-Content -Raw '%~f0'))) -NoNewScope}"`) DO @(
  IF "%%A"=="MVN_CMD" (set __MVNW_CMD__=%%B) ELSE IF "%%B"=="" (echo %%A) ELSE (echo %%A=%%B)
)
@SET PSModulePath=%__MVNW_PSMODULEP_SAVE%
@SET __MVNW_PSMODULEP_SAVE=
@SET __MVNW_ARG0_NAME__=
@SET MVNW_USERNAME=
@SET MVNW_PASSWORD=
@IF NOT "%__MVNW_CMD__%"=="" ("%__MVNW_CMD__%" %*)
@echo Cannot start maven from wrapper >&2 && exit /b 1
@GOTO :EOF
: end batch / begin powershell #>

$ErrorActionPreference = "Stop"
if ($env:MVNW_VERBOSE -eq "true") {
  $VerbosePreference = "Continue"
}

# calculate distributionUrl, requires .mvn/wrapper/maven-wrapper.properties
$distributionUrl = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionUrl
if (!$distributionUrl) {
  Write-Error "cannot read distributionUrl property in $scriptDir/.mvn/wrapper/maven-wrapper.properties"
}

switch -wildcard -casesensitive ( $($distributionUrl -replace '^.*/','') ) {
  "maven-mvnd-*" {
    $USE_MVND = $true
    $distributionUrl = $distributionUrl -replace '-bin\.[^.]*$',"-windows-amd64.zip"
    $MVN_CMD = "mvnd.cmd"
    break
  }
  default {
    $USE_MVND = $false
    $MVN_CMD = $script -replace '^mvnw','mvn'
    break
  }
}

# apply MVNW_REPOURL and calculate MAVEN_HOME
# maven home pattern: ~/.m2/wrapper/dists/{apache-maven-<version>,maven-mvnd-<version>-<platform>}/<hash>
if ($env:MVNW_REPOURL) {
  $MVNW_REPO_PATTERN = if ($USE_MVND -eq $False) { "/org/apache/maven/" } else { "/maven/mvnd/" }
  $distributionUrl = "$env:MVNW_REPOURL$MVNW_REPO_PATTERN$($distributionUrl -replace "^.*$MVNW_REPO_PATTERN",'')"
}
$distributionUrlName = $distributionUrl -replace '^.*/',''
$distributionUrlNameMain = $distributionUrlName -replace '\.[^.]*$','' -replace '-bin$',''

$MAVEN_M2_PATH = "$HOME/.m2"
if ($env:MAVEN_USER_HOME) {
  $MAVEN_M2_PATH = "$env:MAVEN_USER_HOME"
}

if (-not (Test-Path -Path $MAVEN_M2_PATH)) {
    New-Item -Path $MAVEN_M2_PATH -ItemType Directory | Out-Null
}

$MAVEN_WRAPPER_DISTS = $null
if ((Get-Item $MAVEN_M2_PATH).Target[0] -eq $null) {
  $MAVEN_WRAPPER_DISTS = "$MAVEN_M2_PATH/wrapper/dists"
} else {
  $MAVEN_WRAPPER_DISTS = (Get-Item $MAVEN_M2_PATH).Target[0] + "/wrapper/dists"
}

$MAVEN_HOME_PARENT = "$MAVEN_WRAPPER_DISTS/$distributionUrlNameMain"
$MAVEN_HOME_NAME = ([System.Security.Cryptography.SHA256]::Create().ComputeHash([byte[]][char[]]$distributionUrl) | ForEach-Object {$_.ToString("x2")}) -join ''
$MAVEN_HOME = "$MAVEN_HOME_PARENT/$MAVEN_HOME_NAME"

if (Test-Path -Path "$MAVEN_HOME" -PathType Container) {
  Write-Verbose "found existing MAVEN_HOME at $MAVEN_HOME"
  Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"
  exit $?
}

if (! $distributionUrlNameMain -or ($distributionUrlName -eq $distributionUrlNameMain)) {
  Write-Error "distributionUrl is not valid, must end with *-bin.zip, but found $distributionUrl"
}

# prepare tmp dir
$TMP_DOWNLOAD_DIR_HOLDER = New-TemporaryFile
$TMP_DOWNLOAD_DIR = New-Item -Itemtype Directory -Path "$TMP_DOWNLOAD_DIR_HOLDER.dir"
$TMP_DOWNLOAD_DIR_HOLDER.Delete() | Out-Null
trap {
  if ($TMP_DOWNLOAD_DIR.Exists) {
    try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
    catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
  }
}

New-Item -Itemtype Directory -Path "$MAVEN_HOME_PARENT" -Force | Out-Null

# Download and Install Apache Maven
Write-Verbose "Couldn't find MAVEN_HOME, downloading and installing it ..."
Write-Verbose "Downloading from: $distributionUrl"
Write-Verbose "Downloading to: $TMP_DOWNLOAD_DIR/$distributionUrlName"

$webclient = New-Object System.Net.WebClient
if ($env:MVNW_USERNAME -and $env:MVNW_PASSWORD) {
  $webclient.Credentials = New-Object System.Net.NetworkCredential($env:MVNW_USERNAME, $env:MVNW_PASSWORD)
}
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
$webclient.DownloadFile($distributionUrl, "$TMP_DOWNLOAD_DIR/$distributionUrlName") | Out-Null

# If specified, validate the SHA-256 sum of the Maven distribution zip file
$distributionSha256Sum = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionSha256Sum
if ($distributionSha256Sum) {
  if ($USE_MVND) {
    Write-Error "Checksum validation is not supported for maven-mvnd. `nPlease disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties."
  }
  Import-Module $PSHOME\Modules\Microsoft.PowerShell.Utility -Function Get-FileHash
  if ((Get-FileHash "$TMP_DOWNLOAD_DIR/$distributionUrlName" -Algorithm SHA256).Hash.ToLower() -ne $distributionSha256Sum) {
    Write-Error "Error: Failed to validate Maven distribution SHA-256, your Maven distribution might be compromised. If you updated your Maven version, you need to update the specified distributionSha256Sum property."
  }
}

# unzip and move
Expand-Archive "$TMP_DOWNLOAD_DIR/$distributionUrlName" -DestinationPath "$TMP_DOWNLOAD_DIR" | Out-Null

# Find the actual extracted directory name (handles snapshots where filename != directory name)
$actualDistributionDir = ""

# First try the expected directory name (for regular distributions)
$expectedPath = Join-Path "$TMP_DOWNLOAD_DIR" "$distributionUrlNameMain"
$expectedMvnPath = Join-Path "$expectedPath" "bin/$MVN_CMD"
if ((Test-Path -Path $expectedPath -PathType Container) -and (Test-Path -Path $expectedMvnPath -PathType Leaf)) {
  $actualDistributionDir = $distributionUrlNameMain
}

# If not found, search for any directory with the Maven executable (for snapshots)
if (!$actualDistributionDir) {
  Get-ChildItem -Path "$TMP_DOWNLOAD_DIR" -Directory | ForEach-Object {
    $testPath = Join-Path $_.FullName "bin/$MVN_CMD"
    if (Test-Path -Path $testPath -PathType Leaf) {
      $actualDistributionDir = $_.Name
    }
  }
}

if (!$actualDistributionDir) {
  Write-Error "Could not find Maven distribution directory in extracted archive"
}

Write-Verbose "Found extracted Maven distribution directory: $actualDistributionDir"
Rename-Item -Path "$TMP_DOWNLOAD_DIR/$actualDistributionDir" -NewName $MAVEN_HOME_NAME | Out-Null
try {
  Move-Item -Path "$TMP_DOWNLOAD_DIR/$MAVEN_HOME_NAME" -Destination $MAVEN_HOME_PARENT | Out-Null
} catch {
  if (! (Test-Path -Path "$MAVEN_HOME" -PathType Container)) {
    Write-Error "fail to move MAVEN_HOME"
  }
} finally {
  try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
  catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
}

Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"

```

## notification-service\pom.xml

```xml
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
			<groupId>tg.ngstars</groupId>
			<artifactId>ng-fields-shared-lib</artifactId>
			<version>1.0.0-SNAPSHOT</version>
		</dependency>
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

```

## notification-service\.mvn\wrapper\maven-wrapper.properties

```bash
wrapperVersion=3.3.4
distributionType=only-script
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.16/apache-maven-3.9.16-bin.zip

```

## notification-service\src\main\java\tg\ngstars\notification\NotificationServiceApplication.java

```java
package tg.ngstars.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

}

```

## notification-service\src\main\java\tg\ngstars\notification\config\GlobalExceptionHandler.java

```java
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
                fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalide",
                (a, b) -> a + "; " + b
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

```

## notification-service\src\main\java\tg\ngstars\notification\config\SecurityConfig.java

```java
package tg.ngstars.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import tg.ngstars.common.security.RealmRoleConverter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
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
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new RealmRoleConverter());
        return converter;
    }
}

```

## notification-service\src\main\java\tg\ngstars\notification\controller\NotificationController.java

```java
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

```

## notification-service\src\main\java\tg\ngstars\notification\dto\EmailRequest.java

```java
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

```

## notification-service\src\main\java\tg\ngstars\notification\service\EmailService.java

```java
package tg.ngstars.notification.service;

import java.util.HashMap;
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

    // ponytail: add new template names here when creating new .html files in templates/email/
    private static final Set<String> ALLOWED_TEMPLATES = Set.of(
        "intervention-notification", "password-reset");

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

        var vars = new HashMap<String, Object>();
        vars.put("interventionRef", request.interventionRef());
        vars.put("clientName", request.clientName());
        vars.put("equipmentType", request.equipmentType());
        vars.put("status", request.status());
        vars.put("assignedTo", request.assignedTo());
        var ctx = new Context();
        ctx.setVariables(vars);
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

```

## notification-service\src\main\resources\application-dev.yml

```yaml
springdoc:
  swagger-ui:
    enabled: true

logging:
  level:
    tg.ngstars: DEBUG
    org.springframework.security: DEBUG

```

## notification-service\src\main\resources\application-prod.yml

```yaml
springdoc:
  swagger-ui:
    enabled: false
  api-docs:
    enabled: false
logging:
  level:
    tg.ngstars: WARN

```

## notification-service\src\main\resources\application-test.yml

```yaml
spring:
  mail:
    host: localhost
    port: 2525

```

## notification-service\src\main\resources\application.yml

```yaml
server:
  port: 8085
  shutdown: graceful
  forward-headers-strategy: native

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
    enabled: false
    path: /swagger-ui.html

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    env:
      keys-to-sanitize: password,secret,token,credential,smtp

```

## notification-service\src\main\resources\templates\email\intervention-notification.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head><meta charset="UTF-8"/></head>
<body style="font-family: Arial, sans-serif; color: #333;">
    <h2>Notification d'intervention</h2>
    <p th:if="${interventionRef}">R&eacute;f&eacute;rence: <strong th:text="${interventionRef}"></strong></p>
    <p th:if="${clientName}">Client: <strong th:text="${clientName}"></strong></p>
    <p th:if="${equipmentType}">&Eacute;quipement: <strong th:text="${equipmentType}"></strong></p>
    <p th:if="${status}">Statut: <strong th:text="${status}"></strong></p>
    <p th:if="${assignedTo}">Assign&eacute; &agrave;: <strong th:text="${assignedTo}"></strong></p>
    <hr/>
    <p style="color: #888; font-size: 0.9em;">Email automatique - NG-STARs Field Service</p>
</body>
</html>

```

## notification-service\src\test\java\tg\ngstars\notification\NotificationServiceApplicationTests.java

```java
package tg.ngstars.notification;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class NotificationServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}

```

---

########### report-service ###########

## report-service\.env.template

```text
INTERVENTION_SERVICE_URL=http://localhost:8083
KEYCLOAK_ISSUER_URI=http://localhost:8088/realms/ng-fields

```

## report-service\.gitattributes

```text
/mvnw text eol=lf
*.cmd text eol=crlf

```

## report-service\.gitignore

```text
HELP.md
target/
.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

### STS ###
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache

### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr

### NetBeans ###
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/
build/
!**/src/main/**/build/
!**/src/test/**/build/

### VS Code ###
.vscode/

```

## report-service\HELP.md

```markdown
# Read Me First
The following was discovered as part of building this project:

* The original package name 'tg.ngstars.report-service' is invalid and this project uses 'tg.ngstars.report_service' instead.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.1.0/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.1.0/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/4.1.0/reference/web/servlet.html)
* [OAuth2 Resource Server](https://docs.spring.io/spring-boot/4.1.0/reference/web/spring-security.html#web.security.oauth2.server)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/4.1.0/reference/actuator/index.html)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.


```

## report-service\mvnw

```text
#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.4
#
# Optional ENV vars
# -----------------
#   JAVA_HOME - location of a JDK home dir, required when download maven via java source
#   MVNW_REPOURL - repo url base for downloading maven distribution
#   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
#   MVNW_VERBOSE - true: enable verbose log; debug: trace the mvnw script; others: silence the output
# ----------------------------------------------------------------------------

set -euf
[ "${MVNW_VERBOSE-}" != debug ] || set -x

# OS specific support.
native_path() { printf %s\\n "$1"; }
case "$(uname)" in
CYGWIN* | MINGW*)
  [ -z "${JAVA_HOME-}" ] || JAVA_HOME="$(cygpath --unix "$JAVA_HOME")"
  native_path() { cygpath --path --windows "$1"; }
  ;;
esac

# set JAVACMD and JAVACCMD
set_java_home() {
  # For Cygwin and MinGW, ensure paths are in Unix format before anything is touched
  if [ -n "${JAVA_HOME-}" ]; then
    if [ -x "$JAVA_HOME/jre/sh/java" ]; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
      JAVACCMD="$JAVA_HOME/jre/sh/javac"
    else
      JAVACMD="$JAVA_HOME/bin/java"
      JAVACCMD="$JAVA_HOME/bin/javac"

      if [ ! -x "$JAVACMD" ] || [ ! -x "$JAVACCMD" ]; then
        echo "The JAVA_HOME environment variable is not defined correctly, so mvnw cannot run." >&2
        echo "JAVA_HOME is set to \"$JAVA_HOME\", but \"\$JAVA_HOME/bin/java\" or \"\$JAVA_HOME/bin/javac\" does not exist." >&2
        return 1
      fi
    fi
  else
    JAVACMD="$(
      'set' +e
      'unset' -f command 2>/dev/null
      'command' -v java
    )" || :
    JAVACCMD="$(
      'set' +e
      'unset' -f command 2>/dev/null
      'command' -v javac
    )" || :

    if [ ! -x "${JAVACMD-}" ] || [ ! -x "${JAVACCMD-}" ]; then
      echo "The java/javac command does not exist in PATH nor is JAVA_HOME set, so mvnw cannot run." >&2
      return 1
    fi
  fi
}

# hash string like Java String::hashCode
hash_string() {
  str="${1:-}" h=0
  while [ -n "$str" ]; do
    char="${str%"${str#?}"}"
    h=$(((h * 31 + $(LC_CTYPE=C printf %d "'$char")) % 4294967296))
    str="${str#?}"
  done
  printf %x\\n $h
}

verbose() { :; }
[ "${MVNW_VERBOSE-}" != true ] || verbose() { printf %s\\n "${1-}"; }

die() {
  printf %s\\n "$1" >&2
  exit 1
}

trim() {
  # MWRAPPER-139:
  #   Trims trailing and leading whitespace, carriage returns, tabs, and linefeeds.
  #   Needed for removing poorly interpreted newline sequences when running in more
  #   exotic environments such as mingw bash on Windows.
  printf "%s" "${1}" | tr -d '[:space:]'
}

scriptDir="$(dirname "$0")"
scriptName="$(basename "$0")"

# parse distributionUrl and optional distributionSha256Sum, requires .mvn/wrapper/maven-wrapper.properties
while IFS="=" read -r key value; do
  case "${key-}" in
  distributionUrl) distributionUrl=$(trim "${value-}") ;;
  distributionSha256Sum) distributionSha256Sum=$(trim "${value-}") ;;
  esac
done <"$scriptDir/.mvn/wrapper/maven-wrapper.properties"
[ -n "${distributionUrl-}" ] || die "cannot read distributionUrl property in $scriptDir/.mvn/wrapper/maven-wrapper.properties"

case "${distributionUrl##*/}" in
maven-mvnd-*bin.*)
  MVN_CMD=mvnd.sh _MVNW_REPO_PATTERN=/maven/mvnd/
  case "${PROCESSOR_ARCHITECTURE-}${PROCESSOR_ARCHITEW6432-}:$(uname -a)" in
  *AMD64:CYGWIN* | *AMD64:MINGW*) distributionPlatform=windows-amd64 ;;
  :Darwin*x86_64) distributionPlatform=darwin-amd64 ;;
  :Darwin*arm64) distributionPlatform=darwin-aarch64 ;;
  :Linux*x86_64*) distributionPlatform=linux-amd64 ;;
  *)
    echo "Cannot detect native platform for mvnd on $(uname)-$(uname -m), use pure java version" >&2
    distributionPlatform=linux-amd64
    ;;
  esac
  distributionUrl="${distributionUrl%-bin.*}-$distributionPlatform.zip"
  ;;
maven-mvnd-*) MVN_CMD=mvnd.sh _MVNW_REPO_PATTERN=/maven/mvnd/ ;;
*) MVN_CMD="mvn${scriptName#mvnw}" _MVNW_REPO_PATTERN=/org/apache/maven/ ;;
esac

# apply MVNW_REPOURL and calculate MAVEN_HOME
# maven home pattern: ~/.m2/wrapper/dists/{apache-maven-<version>,maven-mvnd-<version>-<platform>}/<hash>
[ -z "${MVNW_REPOURL-}" ] || distributionUrl="$MVNW_REPOURL$_MVNW_REPO_PATTERN${distributionUrl#*"$_MVNW_REPO_PATTERN"}"
distributionUrlName="${distributionUrl##*/}"
distributionUrlNameMain="${distributionUrlName%.*}"
distributionUrlNameMain="${distributionUrlNameMain%-bin}"
MAVEN_USER_HOME="${MAVEN_USER_HOME:-${HOME}/.m2}"
MAVEN_HOME="${MAVEN_USER_HOME}/wrapper/dists/${distributionUrlNameMain-}/$(hash_string "$distributionUrl")"

exec_maven() {
  unset MVNW_VERBOSE MVNW_USERNAME MVNW_PASSWORD MVNW_REPOURL || :
  exec "$MAVEN_HOME/bin/$MVN_CMD" "$@" || die "cannot exec $MAVEN_HOME/bin/$MVN_CMD"
}

if [ -d "$MAVEN_HOME" ]; then
  verbose "found existing MAVEN_HOME at $MAVEN_HOME"
  exec_maven "$@"
fi

case "${distributionUrl-}" in
*?-bin.zip | *?maven-mvnd-?*-?*.zip) ;;
*) die "distributionUrl is not valid, must match *-bin.zip or maven-mvnd-*.zip, but found '${distributionUrl-}'" ;;
esac

# prepare tmp dir
if TMP_DOWNLOAD_DIR="$(mktemp -d)" && [ -d "$TMP_DOWNLOAD_DIR" ]; then
  clean() { rm -rf -- "$TMP_DOWNLOAD_DIR"; }
  trap clean HUP INT TERM EXIT
else
  die "cannot create temp dir"
fi

mkdir -p -- "${MAVEN_HOME%/*}"

# Download and Install Apache Maven
verbose "Couldn't find MAVEN_HOME, downloading and installing it ..."
verbose "Downloading from: $distributionUrl"
verbose "Downloading to: $TMP_DOWNLOAD_DIR/$distributionUrlName"

# select .zip or .tar.gz
if ! command -v unzip >/dev/null; then
  distributionUrl="${distributionUrl%.zip}.tar.gz"
  distributionUrlName="${distributionUrl##*/}"
fi

# verbose opt
__MVNW_QUIET_WGET=--quiet __MVNW_QUIET_CURL=--silent __MVNW_QUIET_UNZIP=-q __MVNW_QUIET_TAR=''
[ "${MVNW_VERBOSE-}" != true ] || __MVNW_QUIET_WGET='' __MVNW_QUIET_CURL='' __MVNW_QUIET_UNZIP='' __MVNW_QUIET_TAR=v

# normalize http auth
case "${MVNW_PASSWORD:+has-password}" in
'') MVNW_USERNAME='' MVNW_PASSWORD='' ;;
has-password) [ -n "${MVNW_USERNAME-}" ] || MVNW_USERNAME='' MVNW_PASSWORD='' ;;
esac

if [ -z "${MVNW_USERNAME-}" ] && command -v wget >/dev/null; then
  verbose "Found wget ... using wget"
  wget ${__MVNW_QUIET_WGET:+"$__MVNW_QUIET_WGET"} "$distributionUrl" -O "$TMP_DOWNLOAD_DIR/$distributionUrlName" || die "wget: Failed to fetch $distributionUrl"
elif [ -z "${MVNW_USERNAME-}" ] && command -v curl >/dev/null; then
  verbose "Found curl ... using curl"
  curl ${__MVNW_QUIET_CURL:+"$__MVNW_QUIET_CURL"} -f -L -o "$TMP_DOWNLOAD_DIR/$distributionUrlName" "$distributionUrl" || die "curl: Failed to fetch $distributionUrl"
elif set_java_home; then
  verbose "Falling back to use Java to download"
  javaSource="$TMP_DOWNLOAD_DIR/Downloader.java"
  targetZip="$TMP_DOWNLOAD_DIR/$distributionUrlName"
  cat >"$javaSource" <<-END
	public class Downloader extends java.net.Authenticator
	{
	  protected java.net.PasswordAuthentication getPasswordAuthentication()
	  {
	    return new java.net.PasswordAuthentication( System.getenv( "MVNW_USERNAME" ), System.getenv( "MVNW_PASSWORD" ).toCharArray() );
	  }
	  public static void main( String[] args ) throws Exception
	  {
	    setDefault( new Downloader() );
	    java.nio.file.Files.copy( java.net.URI.create( args[0] ).toURL().openStream(), java.nio.file.Paths.get( args[1] ).toAbsolutePath().normalize() );
	  }
	}
	END
  # For Cygwin/MinGW, switch paths to Windows format before running javac and java
  verbose " - Compiling Downloader.java ..."
  "$(native_path "$JAVACCMD")" "$(native_path "$javaSource")" || die "Failed to compile Downloader.java"
  verbose " - Running Downloader.java ..."
  "$(native_path "$JAVACMD")" -cp "$(native_path "$TMP_DOWNLOAD_DIR")" Downloader "$distributionUrl" "$(native_path "$targetZip")"
fi

# If specified, validate the SHA-256 sum of the Maven distribution zip file
if [ -n "${distributionSha256Sum-}" ]; then
  distributionSha256Result=false
  if [ "$MVN_CMD" = mvnd.sh ]; then
    echo "Checksum validation is not supported for maven-mvnd." >&2
    echo "Please disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties." >&2
    exit 1
  elif command -v sha256sum >/dev/null; then
    if echo "$distributionSha256Sum  $TMP_DOWNLOAD_DIR/$distributionUrlName" | sha256sum -c - >/dev/null 2>&1; then
      distributionSha256Result=true
    fi
  elif command -v shasum >/dev/null; then
    if echo "$distributionSha256Sum  $TMP_DOWNLOAD_DIR/$distributionUrlName" | shasum -a 256 -c >/dev/null 2>&1; then
      distributionSha256Result=true
    fi
  else
    echo "Checksum validation was requested but neither 'sha256sum' or 'shasum' are available." >&2
    echo "Please install either command, or disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties." >&2
    exit 1
  fi
  if [ $distributionSha256Result = false ]; then
    echo "Error: Failed to validate Maven distribution SHA-256, your Maven distribution might be compromised." >&2
    echo "If you updated your Maven version, you need to update the specified distributionSha256Sum property." >&2
    exit 1
  fi
fi

# unzip and move
if command -v unzip >/dev/null; then
  unzip ${__MVNW_QUIET_UNZIP:+"$__MVNW_QUIET_UNZIP"} "$TMP_DOWNLOAD_DIR/$distributionUrlName" -d "$TMP_DOWNLOAD_DIR" || die "failed to unzip"
else
  tar xzf${__MVNW_QUIET_TAR:+"$__MVNW_QUIET_TAR"} "$TMP_DOWNLOAD_DIR/$distributionUrlName" -C "$TMP_DOWNLOAD_DIR" || die "failed to untar"
fi

# Find the actual extracted directory name (handles snapshots where filename != directory name)
actualDistributionDir=""

# First try the expected directory name (for regular distributions)
if [ -d "$TMP_DOWNLOAD_DIR/$distributionUrlNameMain" ]; then
  if [ -f "$TMP_DOWNLOAD_DIR/$distributionUrlNameMain/bin/$MVN_CMD" ]; then
    actualDistributionDir="$distributionUrlNameMain"
  fi
fi

# If not found, search for any directory with the Maven executable (for snapshots)
if [ -z "$actualDistributionDir" ]; then
  # enable globbing to iterate over items
  set +f
  for dir in "$TMP_DOWNLOAD_DIR"/*; do
    if [ -d "$dir" ]; then
      if [ -f "$dir/bin/$MVN_CMD" ]; then
        actualDistributionDir="$(basename "$dir")"
        break
      fi
    fi
  done
  set -f
fi

if [ -z "$actualDistributionDir" ]; then
  verbose "Contents of $TMP_DOWNLOAD_DIR:"
  verbose "$(ls -la "$TMP_DOWNLOAD_DIR")"
  die "Could not find Maven distribution directory in extracted archive"
fi

verbose "Found extracted Maven distribution directory: $actualDistributionDir"
printf %s\\n "$distributionUrl" >"$TMP_DOWNLOAD_DIR/$actualDistributionDir/mvnw.url"
mv -- "$TMP_DOWNLOAD_DIR/$actualDistributionDir" "$MAVEN_HOME" || [ -d "$MAVEN_HOME" ] || die "fail to move MAVEN_HOME"

clean || :
exec_maven "$@"

```

## report-service\mvnw.cmd

```text
<# : batch portion
@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.4
@REM
@REM Optional ENV vars
@REM   MVNW_REPOURL - repo url base for downloading maven distribution
@REM   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
@REM   MVNW_VERBOSE - true: enable verbose log; others: silence the output
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
@SET PSModulePath=
@FOR /F "usebackq tokens=1* delims==" %%A IN (`powershell -noprofile "& {$scriptDir='%~dp0'; $script='%__MVNW_ARG0_NAME__%'; icm -ScriptBlock ([Scriptblock]::Create((Get-Content -Raw '%~f0'))) -NoNewScope}"`) DO @(
  IF "%%A"=="MVN_CMD" (set __MVNW_CMD__=%%B) ELSE IF "%%B"=="" (echo %%A) ELSE (echo %%A=%%B)
)
@SET PSModulePath=%__MVNW_PSMODULEP_SAVE%
@SET __MVNW_PSMODULEP_SAVE=
@SET __MVNW_ARG0_NAME__=
@SET MVNW_USERNAME=
@SET MVNW_PASSWORD=
@IF NOT "%__MVNW_CMD__%"=="" ("%__MVNW_CMD__%" %*)
@echo Cannot start maven from wrapper >&2 && exit /b 1
@GOTO :EOF
: end batch / begin powershell #>

$ErrorActionPreference = "Stop"
if ($env:MVNW_VERBOSE -eq "true") {
  $VerbosePreference = "Continue"
}

# calculate distributionUrl, requires .mvn/wrapper/maven-wrapper.properties
$distributionUrl = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionUrl
if (!$distributionUrl) {
  Write-Error "cannot read distributionUrl property in $scriptDir/.mvn/wrapper/maven-wrapper.properties"
}

switch -wildcard -casesensitive ( $($distributionUrl -replace '^.*/','') ) {
  "maven-mvnd-*" {
    $USE_MVND = $true
    $distributionUrl = $distributionUrl -replace '-bin\.[^.]*$',"-windows-amd64.zip"
    $MVN_CMD = "mvnd.cmd"
    break
  }
  default {
    $USE_MVND = $false
    $MVN_CMD = $script -replace '^mvnw','mvn'
    break
  }
}

# apply MVNW_REPOURL and calculate MAVEN_HOME
# maven home pattern: ~/.m2/wrapper/dists/{apache-maven-<version>,maven-mvnd-<version>-<platform>}/<hash>
if ($env:MVNW_REPOURL) {
  $MVNW_REPO_PATTERN = if ($USE_MVND -eq $False) { "/org/apache/maven/" } else { "/maven/mvnd/" }
  $distributionUrl = "$env:MVNW_REPOURL$MVNW_REPO_PATTERN$($distributionUrl -replace "^.*$MVNW_REPO_PATTERN",'')"
}
$distributionUrlName = $distributionUrl -replace '^.*/',''
$distributionUrlNameMain = $distributionUrlName -replace '\.[^.]*$','' -replace '-bin$',''

$MAVEN_M2_PATH = "$HOME/.m2"
if ($env:MAVEN_USER_HOME) {
  $MAVEN_M2_PATH = "$env:MAVEN_USER_HOME"
}

if (-not (Test-Path -Path $MAVEN_M2_PATH)) {
    New-Item -Path $MAVEN_M2_PATH -ItemType Directory | Out-Null
}

$MAVEN_WRAPPER_DISTS = $null
if ((Get-Item $MAVEN_M2_PATH).Target[0] -eq $null) {
  $MAVEN_WRAPPER_DISTS = "$MAVEN_M2_PATH/wrapper/dists"
} else {
  $MAVEN_WRAPPER_DISTS = (Get-Item $MAVEN_M2_PATH).Target[0] + "/wrapper/dists"
}

$MAVEN_HOME_PARENT = "$MAVEN_WRAPPER_DISTS/$distributionUrlNameMain"
$MAVEN_HOME_NAME = ([System.Security.Cryptography.SHA256]::Create().ComputeHash([byte[]][char[]]$distributionUrl) | ForEach-Object {$_.ToString("x2")}) -join ''
$MAVEN_HOME = "$MAVEN_HOME_PARENT/$MAVEN_HOME_NAME"

if (Test-Path -Path "$MAVEN_HOME" -PathType Container) {
  Write-Verbose "found existing MAVEN_HOME at $MAVEN_HOME"
  Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"
  exit $?
}

if (! $distributionUrlNameMain -or ($distributionUrlName -eq $distributionUrlNameMain)) {
  Write-Error "distributionUrl is not valid, must end with *-bin.zip, but found $distributionUrl"
}

# prepare tmp dir
$TMP_DOWNLOAD_DIR_HOLDER = New-TemporaryFile
$TMP_DOWNLOAD_DIR = New-Item -Itemtype Directory -Path "$TMP_DOWNLOAD_DIR_HOLDER.dir"
$TMP_DOWNLOAD_DIR_HOLDER.Delete() | Out-Null
trap {
  if ($TMP_DOWNLOAD_DIR.Exists) {
    try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
    catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
  }
}

New-Item -Itemtype Directory -Path "$MAVEN_HOME_PARENT" -Force | Out-Null

# Download and Install Apache Maven
Write-Verbose "Couldn't find MAVEN_HOME, downloading and installing it ..."
Write-Verbose "Downloading from: $distributionUrl"
Write-Verbose "Downloading to: $TMP_DOWNLOAD_DIR/$distributionUrlName"

$webclient = New-Object System.Net.WebClient
if ($env:MVNW_USERNAME -and $env:MVNW_PASSWORD) {
  $webclient.Credentials = New-Object System.Net.NetworkCredential($env:MVNW_USERNAME, $env:MVNW_PASSWORD)
}
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
$webclient.DownloadFile($distributionUrl, "$TMP_DOWNLOAD_DIR/$distributionUrlName") | Out-Null

# If specified, validate the SHA-256 sum of the Maven distribution zip file
$distributionSha256Sum = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionSha256Sum
if ($distributionSha256Sum) {
  if ($USE_MVND) {
    Write-Error "Checksum validation is not supported for maven-mvnd. `nPlease disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties."
  }
  Import-Module $PSHOME\Modules\Microsoft.PowerShell.Utility -Function Get-FileHash
  if ((Get-FileHash "$TMP_DOWNLOAD_DIR/$distributionUrlName" -Algorithm SHA256).Hash.ToLower() -ne $distributionSha256Sum) {
    Write-Error "Error: Failed to validate Maven distribution SHA-256, your Maven distribution might be compromised. If you updated your Maven version, you need to update the specified distributionSha256Sum property."
  }
}

# unzip and move
Expand-Archive "$TMP_DOWNLOAD_DIR/$distributionUrlName" -DestinationPath "$TMP_DOWNLOAD_DIR" | Out-Null

# Find the actual extracted directory name (handles snapshots where filename != directory name)
$actualDistributionDir = ""

# First try the expected directory name (for regular distributions)
$expectedPath = Join-Path "$TMP_DOWNLOAD_DIR" "$distributionUrlNameMain"
$expectedMvnPath = Join-Path "$expectedPath" "bin/$MVN_CMD"
if ((Test-Path -Path $expectedPath -PathType Container) -and (Test-Path -Path $expectedMvnPath -PathType Leaf)) {
  $actualDistributionDir = $distributionUrlNameMain
}

# If not found, search for any directory with the Maven executable (for snapshots)
if (!$actualDistributionDir) {
  Get-ChildItem -Path "$TMP_DOWNLOAD_DIR" -Directory | ForEach-Object {
    $testPath = Join-Path $_.FullName "bin/$MVN_CMD"
    if (Test-Path -Path $testPath -PathType Leaf) {
      $actualDistributionDir = $_.Name
    }
  }
}

if (!$actualDistributionDir) {
  Write-Error "Could not find Maven distribution directory in extracted archive"
}

Write-Verbose "Found extracted Maven distribution directory: $actualDistributionDir"
Rename-Item -Path "$TMP_DOWNLOAD_DIR/$actualDistributionDir" -NewName $MAVEN_HOME_NAME | Out-Null
try {
  Move-Item -Path "$TMP_DOWNLOAD_DIR/$MAVEN_HOME_NAME" -Destination $MAVEN_HOME_PARENT | Out-Null
} catch {
  if (! (Test-Path -Path "$MAVEN_HOME" -PathType Container)) {
    Write-Error "fail to move MAVEN_HOME"
  }
} finally {
  try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
  catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
}

Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"

```

## report-service\pom.xml

```xml
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
			<groupId>tg.ngstars</groupId>
			<artifactId>ng-fields-shared-lib</artifactId>
			<version>1.0.0-SNAPSHOT</version>
		</dependency>
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

```

## report-service\.mvn\wrapper\maven-wrapper.properties

```bash
wrapperVersion=3.3.4
distributionType=only-script
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.16/apache-maven-3.9.16-bin.zip

```

## report-service\src\main\java\tg\ngstars\report\ReportServiceApplication.java

```java
package tg.ngstars.report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReportServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportServiceApplication.class, args);
	}

}

```

## report-service\src\main\java\tg\ngstars\report\client\InterventionClient.java

```java
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

```

## report-service\src\main\java\tg\ngstars\report\config\GlobalExceptionHandler.java

```java
package tg.ngstars.report.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

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

```

## report-service\src\main\java\tg\ngstars\report\config\SecurityConfig.java

```java
package tg.ngstars.report.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import tg.ngstars.common.security.RealmRoleConverter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
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
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new RealmRoleConverter());
        return converter;
    }
}

```

## report-service\src\main\java\tg\ngstars\report\controller\ReportController.java

```java
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

```

## report-service\src\main\java\tg\ngstars\report\service\ReportService.java

```java
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

```

## report-service\src\main\resources\application-dev.yml

```yaml
springdoc:
  swagger-ui:
    enabled: true

logging:
  level:
    tg.ngstars: DEBUG

```

## report-service\src\main\resources\application-prod.yml

```yaml
springdoc:
  swagger-ui:
    enabled: false
  api-docs:
    enabled: false
logging:
  level:
    tg.ngstars: WARN

```

## report-service\src\main\resources\application.yml

```yaml
server:
  port: 8086
  shutdown: graceful
  forward-headers-strategy: native

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
    enabled: false
    path: /swagger-ui.html

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    env:
      keys-to-sanitize: password,secret,token

```

## report-service\src\test\java\tg\ngstars\report\ReportServiceApplicationTests.java

```java
package tg.ngstars.report;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReportServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}

```

