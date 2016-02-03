# vertx
Exploring Vertx and RAML.

1) Creates a HTTP connection for the RAML file defined.<br />
2) Reads the contents of the RAML file and creates mock web services for the path defined in RAML.<br />
3) Finally, returns the example provided in the RAML as the output.<br />


Add below repo and profiles in your .m2/settings.xml for downloading raml parsers

	<repository>
                <id>mulesoft-releases</id>
                <name>MuleSoft Repository</name>
                <url>http://repository.mulesoft.org/releases/</url>
                <layout>default</layout>
            </repository>
            <repository>
                <id>mulesoft-snapshots</id>
                <name>MuleSoft Snapshot Repository</name>
                <url>http://repository.mulesoft.org/snapshots/</url>
                <layout>default</layout>
            </repository>





	<profile>
            <id>Mule</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <repositories>
                <repository>
                    <id>MuleRepository</id>
                    <name>MuleRepository</name>
                    <url>https://repository.mulesoft.org/nexus-ee/content/repositories/releases-ee/</url>
                    <layout>default</layout>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
            </repositories>
        </profile>



  	<activeProfiles>
	     <activeProfile>Mule</activeProfile>
	</activeProfiles>

