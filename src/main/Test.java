package main;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Following:
 * Elegant objects (vol 1.)
 * https://www.nikialeksey.com/2021/09/29/code-analyser.html
 *
 * Intellij github code sample:
 * https://github.com/JetBrains/intellij-sdk-code-samples/blob/main/comparing_references_inspection/src/main/java/org/intellij/sdk/codeInspection/ComparingReferencesInspection.java
 *
 * Documentation:
 * https://plugins.jetbrains.com/docs/intellij/code-inspections.html#inspection-implementation-java-class
 *
 * TODO:
 *  Add enum code checking following: https://www.g4s8.wtf/posts/2020-12-26-enum-objects/
 *  Add LocalQuickFix to holder.registerProblems(...) or description of problems
 */
public class Test extends AbstractBaseJavaLocalInspectionTool {
    private final String FINAL_MODIFIER_PROPERTY = "final";

    @Override
    public @NotNull
    PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            // Class fields
            @Override
            public void visitField(PsiField field) {
                super.visitField(field);
                if (!Objects.requireNonNull(field.getModifierList()).hasModifierProperty(FINAL_MODIFIER_PROPERTY)) {
                    holder.registerProblem(
                            field.getOriginalElement(),
                            "Must be final");
                }
            }

            // Using for loops (for now)
            @Override
            public void visitDeclarationStatement(PsiDeclarationStatement statement) {
                super.visitDeclarationStatement(statement);
                final String statementText = statement.getText();
                if (!statementText.contains("final")
                        && statementText.matches("\\w+\\s*\\w+\\s*=\\s*[\\w|\\d]\\s*;\\s*")) {
                    holder.registerProblem(
                            statement,
                            statement.getText() + " may be final.");
                }
            }

            @Override
            public void visitReturnStatement(PsiReturnStatement statement) {
                super.visitReturnStatement(statement);
                if(statement.getText().matches("\\s*return\\s*null\\s*;\\s*")) {
                    holder.registerProblem(
                            statement,
                            "You must not return 'null' value");
                }
            }

            @Override
            public void visitMethod(PsiMethod method) {
                super.visitMethod(method);
                Pattern pattern = Pattern.compile("(\\s*return\\s*[\\d|\\w]\\s*;\\s*)");
                if (Objects.nonNull(method.getBody())) {
                    for (PsiStatement statement : method.getBody().getStatements()) {
                        Matcher matcher = pattern.matcher(statement.getText());
                        final AtomicInteger count = new AtomicInteger();
                        while (matcher.find()) {
                            count.incrementAndGet();
                        }
                        if (count.get() > 0) {
                            holder.registerProblem(
                                    statement.getOriginalElement(),
                                    "Method has to have only one 'return' statement!");
                        }
                    }
                }
            }
        };
    }
}
