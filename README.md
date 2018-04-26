# oxygen-pjt-beautifier-maven-plugin
A maven plugin to beautify Oxygen project files

## Usage

Just add this plugin to your pom.xml :

      <plugin>
        <groupId>top.marchand.xml.maven</groupId>
        <artifactId>oxygen-pjt-beautifier-maven-plugin</artifactId>
        <version>1.0.1</version>
        <executions>
          <execution>
            <goals><goal>beautify</goal></goals>
          </execution>
        </executions>
      </plugin>

## Parameters

### keepOldFiles

If you want to keep the original files, just set this property to true.

### activateXslLogs

If you need to debug the XSL that beautify the project, set this property to true.
This will generate various intermediate files.

Only useful when debugging the XSL
