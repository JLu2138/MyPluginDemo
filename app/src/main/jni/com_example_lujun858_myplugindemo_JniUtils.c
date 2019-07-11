//
// Created by JLu on 2019-07-10.
//

#include "com_example_lujun858_myplugindemo_JniUtils.h"

JNIEXPORT jstring JNICALL Java_com_example_lujun858_myplugindemo_JniUtils_getString
  (JNIEnv *env, jobject obj) {
    return (*env) ->NewStringUTF(env, "Hello world!");
  }