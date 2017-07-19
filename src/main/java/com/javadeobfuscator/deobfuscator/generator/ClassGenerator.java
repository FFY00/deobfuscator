package com.javadeobfuscator.deobfuscator.generator;

import com.javadeobfuscator.deobfuscator.org.objectweb.asm.ClassWriter;
import com.javadeobfuscator.deobfuscator.org.objectweb.asm.Label;
import com.javadeobfuscator.deobfuscator.org.objectweb.asm.MethodVisitor;
import com.javadeobfuscator.deobfuscator.org.objectweb.asm.Opcodes;
import com.javadeobfuscator.deobfuscator.org.objectweb.asm.tree.*;

public class ClassGenerator {

    protected ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    protected MethodVisitor mv;

    /*
     * Initializes the class
     */
    public ClassGenerator(){
        cw.visit(Opcodes.V1_7,                              // Java 1.7
                Opcodes.ACC_PUBLIC,                         // public class
                "dynamic/DynamicCalculatorImpl",         // package and name
                null,                                   // signature (null means not generic)
                "java/lang/Object",                     // superclass
                new String[]{ "me/ffy00" });                // interfaces

        mv = cw.visitMethod(
                Opcodes.ACC_PUBLIC,                         // public method
                "<init>",                                // method name
                "()V",                                  // descriptor
                null,                                   // signature (null means not generic)
                null);                               // exceptions (array of strings)

        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);

        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,           // Invoke an instance method (non-virtual)
                "java/lang/Object",                      // Class on which the method is defined
                "<init>",                               // Name of the method
                "()V",                                  // Descriptor
                false);                                  // Is this class an interface?

        mv.visitInsn(Opcodes.RETURN);                       // End the constructor method
        mv.visitMaxs(1, 1);                          // Specify max stack and local vars
    }

    // Copies the method to the new class
    public void setDecryptorMethod(MethodNode m){
        MethodVisitor mv = cw.visitMethod(
                Opcodes.ACC_PUBLIC,                         // public method
                "<clinit>",                              // name
                "()V",                                  // descriptor
                null,                                   // signature (null means not generic)
                null);                               // exceptions (array of strings)

        mv.visitCode();

        // Copy the method
        InsnList inList = m.instructions;
        for(int i = 0; i < inList.size(); i++){
            switch (inList.get(i).getType()) {
                case AbstractInsnNode.INSN: {
                    InsnNode insn = (InsnNode) inList.get(i);
                    mv.visitInsn(insn.getOpcode());
                    break;
                }
                case AbstractInsnNode.INT_INSN: {
                    IntInsnNode int_insn = (IntInsnNode) inList.get(i);
                    mv.visitIntInsn(int_insn.getOpcode(), int_insn.operand);
                    break;
                }
                case AbstractInsnNode.VAR_INSN: {
                    VarInsnNode var_insn = (VarInsnNode) inList.get(i);
                    mv.visitVarInsn(var_insn.getOpcode(), var_insn.var);
                    break;
                }
                case AbstractInsnNode.TYPE_INSN: {
                    TypeInsnNode type_insn = (TypeInsnNode) inList.get(i);
                    mv.visitTypeInsn(type_insn.getOpcode(), type_insn.desc);
                    break;
                }
                case AbstractInsnNode.FIELD_INSN: {
                    FieldInsnNode field_insn = (FieldInsnNode) inList.get(i);
                    mv.visitFieldInsn(field_insn.getOpcode(), field_insn.owner, field_insn.name, field_insn.desc);
                    break;
                }
                case AbstractInsnNode.METHOD_INSN: {
                    MethodInsnNode method_insn = (MethodInsnNode) inList.get(i);
                    mv.visitMethodInsn(method_insn.getOpcode(), method_insn.owner, method_insn.name, method_insn.desc, method_insn.itf);
                    break;
                }
                case AbstractInsnNode.INVOKE_DYNAMIC_INSN: {
                    InvokeDynamicInsnNode id_insn = (InvokeDynamicInsnNode) inList.get(i);
                    mv.visitInvokeDynamicInsn(id_insn.name, id_insn.desc, id_insn.bsm, id_insn.bsmArgs);
                    break;
                }
                case AbstractInsnNode.JUMP_INSN: {
                    JumpInsnNode jmp_insn = (JumpInsnNode) inList.get(i);
                    mv.visitJumpInsn(jmp_insn.getOpcode(), jmp_insn.label.getLabel());
                    break;
                }
                case AbstractInsnNode.LABEL: {
                    LabelNode label = (LabelNode) inList.get(i);
                    mv.visitLabel(label.getLabel());
                    break;
                }
                case AbstractInsnNode.LDC_INSN: {
                    LdcInsnNode ldc_insn = (LdcInsnNode) inList.get(i);
                    mv.visitLdcInsn(ldc_insn.cst);
                    break;
                }
                case AbstractInsnNode.IINC_INSN: {
                    IincInsnNode iinc_insn = (IincInsnNode) inList.get(i);
                    mv.visitIincInsn(iinc_insn.var, iinc_insn.incr);
                    break;
                }
                case AbstractInsnNode.TABLESWITCH_INSN: {
                    TableSwitchInsnNode ts_insn = (TableSwitchInsnNode) inList.get(i);
                    mv.visitTableSwitchInsn(ts_insn.min, ts_insn.max, ts_insn.dflt.getLabel(), (Label[]) ts_insn.labels.toArray()); // Casting ts_insn.labels to Label || ASM is not casting the Label type when declaring the List in some versions || Needs tetsing !!!
                    break;
                }
                case AbstractInsnNode.LOOKUPSWITCH_INSN: {
                    LookupSwitchInsnNode ls_insn = (LookupSwitchInsnNode) inList.get(i);
                    Label[] ls_labels = new Label[ls_insn.labels.size()];
                    int[] ls_keys = new int[ls_insn.keys.size()];
                    for (int l = 0; l < ls_keys.length; ++l) {
                        ls_keys[l] = (Integer) ls_insn.keys.get(l); // Casting to Integer || ASM is not casting the Integer type when declaring the List in some versions, make sure your version
                    }
                    mv.visitLookupSwitchInsn(ls_insn.dflt.getLabel(), ls_keys, (Label[]) ls_insn.labels.toArray()); // Casting ls_insn.labels to Label || ASM is not casting the Label type when declaring the List in some versions || Needs tetsing !!!
                    break;
                }
                case AbstractInsnNode.MULTIANEWARRAY_INSN: {
                    InsnNode insn = (InsnNode) inList.get(i);
                    mv.visitInsn(insn.getOpcode());
                    break;
                }
                case AbstractInsnNode.FRAME: {
                    FrameNode frame = (FrameNode) inList.get(i);
                    mv.visitFrame(frame.type, frame.local.toArray().length, frame.local.toArray(), frame.stack.toArray().length, frame.stack.toArray());
                    break;
                }
                case AbstractInsnNode.LINE: {
                    LineNumberNode ln_insn = (LineNumberNode) inList.get(i);
                    mv.visitLineNumber(ln_insn.line, ln_insn.start.getLabel());
                    break;
                }

            }
        }
    }

}
