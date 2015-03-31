

/* First created by JCasGen Sun Apr 27 16:40:19 EDT 2014 */
package edu.cmu.lti.oaqa.watson.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** 
 * Updated by JCasGen Sun Apr 27 16:40:19 EDT 2014
 * XML source: /home/diwang/Dropbox/oaqa-workspace/watson-cloud/src/main/resources/WatsonCloudTypeSystem.xml
 * @generated */
public class Response extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Response.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Response() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Response(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Response(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: responsedJson

  /** getter for responsedJson - gets 
   * @generated
   * @return value of the feature 
   */
  public String getResponsedJson() {
    if (Response_Type.featOkTst && ((Response_Type)jcasType).casFeat_responsedJson == null)
      jcasType.jcas.throwFeatMissing("responsedJson", "edu.cmu.lti.oaqa.watson.types.Response");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Response_Type)jcasType).casFeatCode_responsedJson);}
    
  /** setter for responsedJson - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setResponsedJson(String v) {
    if (Response_Type.featOkTst && ((Response_Type)jcasType).casFeat_responsedJson == null)
      jcasType.jcas.throwFeatMissing("responsedJson", "edu.cmu.lti.oaqa.watson.types.Response");
    jcasType.ll_cas.ll_setStringValue(addr, ((Response_Type)jcasType).casFeatCode_responsedJson, v);}    
  }

    