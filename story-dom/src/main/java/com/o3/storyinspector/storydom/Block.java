//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2025.05.30 at 01:00:27 PM CEST 
//


package com.o3.storyinspector.storydom;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}Emotion" maxOccurs="unbounded"/&gt;
 *         &lt;element ref="{}Body"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="wordCount" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="sentimentScore" type="{http://www.w3.org/2001/XMLSchema}decimal" /&gt;
 *       &lt;attribute name="fkGrade" type="{http://www.w3.org/2001/XMLSchema}decimal" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "emotions",
    "body"
})
@XmlRootElement(name = "Block")
public class Block
    implements Serializable
{

    private final static long serialVersionUID = -1L;
    @XmlElement(name = "Emotion", required = true)
    protected List<Emotion> emotions;
    @XmlElement(name = "Body", required = true)
    protected String body;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "wordCount")
    protected String wordCount;
    @XmlAttribute(name = "sentimentScore")
    protected BigDecimal sentimentScore;
    @XmlAttribute(name = "fkGrade")
    protected BigDecimal fkGrade;

    /**
     * Gets the value of the emotions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the emotions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEmotions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Emotion }
     * 
     * 
     */
    public List<Emotion> getEmotions() {
        if (emotions == null) {
            emotions = new ArrayList<Emotion>();
        }
        return this.emotions;
    }

    /**
     * Gets the value of the body property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the value of the body property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBody(String value) {
        this.body = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the wordCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWordCount() {
        return wordCount;
    }

    /**
     * Sets the value of the wordCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWordCount(String value) {
        this.wordCount = value;
    }

    /**
     * Gets the value of the sentimentScore property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSentimentScore() {
        return sentimentScore;
    }

    /**
     * Sets the value of the sentimentScore property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSentimentScore(BigDecimal value) {
        this.sentimentScore = value;
    }

    /**
     * Gets the value of the fkGrade property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getFkGrade() {
        return fkGrade;
    }

    /**
     * Sets the value of the fkGrade property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setFkGrade(BigDecimal value) {
        this.fkGrade = value;
    }

}
