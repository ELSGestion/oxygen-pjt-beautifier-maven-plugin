/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package top.marchand.xml.maven.oxygenBeautifier;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.Configuration;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XsltTransformer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * This plugin beautify all oxygen projects found in project directory.
 * Mainly, it sorts transformation scenarios. This is mainly to avoid conflicts
 * when project files are commit in SCM, and modified by various users
 * @author cmarchand
 */
@Mojo(name = "beautify", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class BeautifierMojo extends AbstractMojo {
    
    @Component
    public MavenProject project;

    /**
     * If set to true, beautifier XSL will output various intermediate steps.
     * Only useful for XSL debuging
     */
    @Parameter(defaultValue = "false")
    public boolean activateXslLogs;
    
    @Parameter(defaultValue = "false")
    public boolean keepOldFiles;
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        File baseDir = project.getBasedir();
        Processor processor = new Processor(Configuration.newConfiguration());
        try {
            XsltTransformer xsl = processor.newXsltCompiler().compile(new StreamSource(this.getClass().getClassLoader().getResourceAsStream("top/marchand/xml/maven/oxygenBeautifier/projectBeautifier.xsl"))).load();
            xsl.setParameter(new QName("debug.reorder.xpr"), new XdmAtomicValue(isActivateXslLogs()));
            for(File f: getOxygenProjectFiles(baseDir)) {
                String filename = f.getName();
                File originFile = new File(baseDir, getOriginFileName(filename));
                f.renameTo(originFile);
                xsl.setSource(new StreamSource(originFile));
                xsl.setDestination(processor.newSerializer(new File(baseDir, filename)));
                xsl.transform();
                if(!keepOldFiles) originFile.delete();
            }
        } catch (SaxonApiException ex) {
            getLog().error("Unable to load beautifier XSL");
            throw new MojoFailureException("Unable to load beautifier XSL", ex);
        }
    }
    
    private String getOriginFileName(String filename) {
        // remove the .xpr
        String mainPart = filename.substring(0, filename.length()-4);
        return mainPart.concat("-origin.xpr_");
    }
    
    private List<File> getOxygenProjectFiles(final File dir) {
        return Arrays.asList(dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xpr");
            }
        }));
    }

    public boolean isActivateXslLogs() {
        return activateXslLogs;
    }
    
}
