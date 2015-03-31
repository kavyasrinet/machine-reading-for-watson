

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
public class Question extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Question.class);
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
  protected Question() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Question(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Question(JCas jcas) {
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
  //* Feature: questionText

  /** getter for questionText - gets 
   * @generated
   * @return value of the feature 
   */
  public String getQuestionText() {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_questionText == null)
      jcasType.jcas.throwFeatMissing("questionText", "edu.cmu.lti.oaqa.watson.types.Question");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Question_Type)jcasType).casFeatCode_questionText);}
    
  /** setter for questionText - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setQuestionText(String v) {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_questionText == null)
      jcasType.jcas.throwFeatMissing("questionText", "edu.cmu.lti.oaqa.watson.types.Question");
    jcasType.ll_cas.ll_setStringValue(addr, ((Question_Type)jcasType).casFeatCode_questionText, v);}    
   
    
  //*--------------*
  //* Feature: items

  /** getter for items - gets 
   * @generated
   * @return value of the feature 
   */
  public int getItems() {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_items == null)
      jcasType.jcas.throwFeatMissing("items", "edu.cmu.lti.oaqa.watson.types.Question");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Question_Type)jcasType).casFeatCode_items);}
    
  /** setter for items - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setItems(int v) {
    if (Question_Type.featOkTst && ((Question_Type)jcasType).casFeat_items == null)
      jcasType.jcas.throwFeatMissing("items", "edu.cmu.lti.oaqa.watson.types.Question");
    jcasType.ll_cas.ll_setIntValue(addr, ((Question_Type)jcasType).casFeatCode_items, v);}    
  }

    