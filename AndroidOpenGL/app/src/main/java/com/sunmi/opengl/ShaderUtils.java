package com.sunmi.opengl;

import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

public  class ShaderUtils {
        static final String TAG = "ShaderUtils";

        public static int compileShaderCode(int type ,  String sourceCode){
            final int shaderObjectId = glCreateShader(type);
            if (shaderObjectId == 0){
                Log.e(TAG , "can not create sahder");
                return 0;
            }
            glShaderSource(shaderObjectId , sourceCode);
            glCompileShader(shaderObjectId);
            final int[] compileStatus = new int[1];
            glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0){
                Log.e(TAG , "compile fail");
            }
            return shaderObjectId;
        }
        public static int compileVertexShader(String sourceCode){
            return compileShaderCode(GL_VERTEX_SHADER , sourceCode);
        }
        public static int compileFragmentShader(String sourceCode){
            return compileShaderCode(GL_FRAGMENT_SHADER , sourceCode);
        }
        public static int linkProgram(int vertexShaderId , int fragmentShaderId){
            int programId = glCreateProgram();

            if (programId == 0){
                Log.e(TAG , "create program fail");
                return 0;
            }
            glAttachShader(programId , vertexShaderId);
            glAttachShader(programId , fragmentShaderId);
            glLinkProgram(programId);

            final int[] linkStatus = new int[1];
            glGetProgramiv(programId , GL_LINK_STATUS , linkStatus , 0);
            if (linkStatus[0] == 0){
                Log.e(TAG , "link fail");
                return 0;
            }

            return programId;
        }
        public static boolean vaildProgram(int programId){
            glValidateProgram(programId);

            final int[] validateStatus = new int[1];
            glGetProgramiv(programId, GL_VALIDATE_STATUS, validateStatus, 0);
            return validateStatus[0] != 0;
        }
}

