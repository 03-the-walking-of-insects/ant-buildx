package com.github.jacobcn.antkits;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlTask extends Task {

    // parameter file
    private String file;
    // nested node or attr
    private List<ParamNodeOrAttr> attrParams;

    {
        attrParams = new ArrayList<>();
    }

    @Override
    public void execute() throws BuildException {
        validation();
        prepare();
        System.out.println(this.file == null ? "xx" : this.file);
        for (ParamNodeOrAttr attr : attrParams) {
            log("attr -> " + attr.getXpath() + " ," + attr.getName() + " ," + attr.getValue());
        }
        // print original file content
        printXmlFileContent("Print original file content", this.file);

        // backup file
        String bakfile = this.file + ".bak";
        try {
            FileUtils.copyFile(new File(this.file), new File(bakfile));
        } catch (IOException e) {
            throw new BuildException("backup xml file error.", e);
        }
        // update xml node and attr and write to dest file. write xml file
        updateXmlFile(this.file, this.attrParams);

        // print file content if success
        printXmlFileContent("Print new file content", this.file);


    }

    private void updateXmlFile(String filename, List<ParamNodeOrAttr> attrs) {
        String srcFilename = this.file;
        String destFilename = this.file;
        Map<String, Map<String, String>> attrsMap = new HashMap<>();
        for (ParamNodeOrAttr attr : attrs) {
            Map<String, String> attrMap = null;
            if (attrsMap.containsKey(attr.getXpath())) {
                attrMap = attrsMap.get(attr.getXpath());
                attrMap.put(attr.getName(), attr.getValue());
            } else {
                attrMap = new HashMap<>();
                attrMap.put(attr.getName(), attr.getValue());
                attrsMap.put(attr.getXpath(), attrMap);
            }
        }
        try {
            SimpleXmlUpdater.updateAttrs(srcFilename, destFilename, attrsMap);
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            throw new BuildException("update xml file failed.", e);
        }
    }

    private void prepare() {
        for(ParamNodeOrAttr attr : this.attrParams) {
            String newValue = getProject().replaceProperties(attr.getValue().trim());
            attr.setValue(newValue);
        }
    }

    private void printXmlFileContent(String msg, String filename) {
        log("\n" + msg + "\n");
        String originalFileContent = null;
        try {
            originalFileContent = FileUtils.readFileToString(new File(filename));
        } catch (IOException e) {
            throw new BuildException("Read xml file error", e);
        }
        log("----------------------------------");
        log(originalFileContent);
        log("----------------------------------");
    }

    private void validation() throws BuildException {
        if (this.file == null) {
            throw new BuildException("You must set parameter 'file' for the task");
        }
        if (!(new File(file).exists())) {
            throw new BuildException("The file you set up do not exist.");
        }
        for (ParamNodeOrAttr attr : attrParams) {
            if (attr.getXpath() == null || attr.getName() == null || attr.getValue() == null) {
                throw new BuildException("You must set 'xpath', 'attr' and 'value' for <attr>. ");
            }
        }
    }

    public void setFile(String file) {
        this.file = file;
    }

    public ParamNodeOrAttr createAttr() {
        ParamNodeOrAttr attr = new ParamNodeOrAttr();
        attrParams.add(attr);
        return attr;
    }

}
