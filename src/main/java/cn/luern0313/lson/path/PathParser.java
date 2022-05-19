package cn.luern0313.lson.path;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import cn.luern0313.lson.exception.PathParseException;
import cn.luern0313.lson.util.CharReaderUtil;

import static cn.luern0313.lson.path.Status.STATUS_EXPECT_COLON;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_COMMA;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_END_DOCUMENT;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_EXPRESSION_ELEMENT;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_EXPRESSION_END;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_EXPRESSION_START;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_FILTER_COMPARISON;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_FILTER_END;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_FILTER_SINGLE_STRING;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_FILTER_START;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_JSON_CURRENT;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_JSON_ROOT;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_NUMBER;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_PATH_POINT;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_POINT;
import static cn.luern0313.lson.path.Status.STATUS_EXPECT_SYNTAX_ASTERISK;

/**
 * 被 luern0313 创建于 2020/8/8.
 */

public class PathParser {
    final TokenReader reader;

    public PathParser(Reader reader) {
        this.reader = new TokenReader(new CharReaderUtil(reader));
    }

    private static boolean hasStatus(int status, int expectedStatus) {
        return ((status & expectedStatus) > 0);
    }

    public static ArrayList<Object> parse(String path) {
        return parse(new StringReader(path));
    }

    private static ArrayList<Object> parse(Reader r) {
        TokenReader reader = new TokenReader(new CharReaderUtil(r));
        Stack stack = new Stack();
        stack.push(StackValue.newBasePath());
        int status = STATUS_EXPECT_JSON_ROOT.index | STATUS_EXPECT_JSON_CURRENT.index | STATUS_EXPECT_EXPRESSION_START.index | STATUS_EXPECT_PATH_POINT.index;
        while (true) {
            TokenType currentToken = reader.readNextToken();
            switch (currentToken) {
                case JSON_ROOT:
                    if (hasStatus(status, STATUS_EXPECT_JSON_ROOT.index)) {
                        PathType.PathJsonRoot pathJsonRoot = new PathType.PathJsonRoot();
                        if (stack.getTopValueType() != StackValue.TYPE_FILTER) {
                            stack.peek().add(pathJsonRoot);
                            status = STATUS_EXPECT_POINT.index | STATUS_EXPECT_EXPRESSION_START.index | STATUS_EXPECT_END_DOCUMENT.index;
                        } else {
                            StackValue.Filter filter = stack.peek(StackValue.TYPE_FILTER).valueAsFilter();
                            filter.getCurrentPart().changeMode(PathType.PathFilter.PathFilterPart.FilterPartMode.PATH);
                            stack.peek().add(pathJsonRoot);
                            status = STATUS_EXPECT_POINT.index | STATUS_EXPECT_EXPRESSION_START.index | STATUS_EXPECT_FILTER_END.index;
                        }
                        continue;
                    }
                    throw new PathParseException("Unexpected $", reader.reader.getErrorMessage());
                case JSON_CURRENT:
                    if (hasStatus(status, STATUS_EXPECT_JSON_CURRENT.index)) {
                        PathType.PathJsonCurrent pathJsonCurrent = new PathType.PathJsonCurrent();
                        if (stack.getTopValueType() != StackValue.TYPE_FILTER) {
                            stack.peek().add(pathJsonCurrent);
                            status = STATUS_EXPECT_POINT.index | STATUS_EXPECT_EXPRESSION_START.index | STATUS_EXPECT_END_DOCUMENT.index;
                        } else {
                            StackValue.Filter filter = stack.peek(StackValue.TYPE_FILTER).valueAsFilter();
                            filter.getCurrentPart().changeMode(PathType.PathFilter.PathFilterPart.FilterPartMode.PATH);
                            stack.peek().add(pathJsonCurrent);
                            status = STATUS_EXPECT_POINT.index | STATUS_EXPECT_EXPRESSION_START.index | STATUS_EXPECT_FILTER_END.index;
                        }
                        continue;
                    }
                    throw new PathParseException("Unexpected @", reader.reader.getErrorMessage());
                case SPLIT_POINT:
                    if (hasStatus(status, STATUS_EXPECT_POINT.index)) {
                        status = STATUS_EXPECT_PATH_POINT.index;
                        continue;
                    }
                    throw new PathParseException("Unexpected .", reader.reader.getErrorMessage());
                case STRING:
                    if (hasStatus(status, STATUS_EXPECT_EXPRESSION_ELEMENT.index)) {
                        String string = reader.readString(true, true);
                        if (stack.getTopValueType() == StackValue.TYPE_EXPRESSION) {
                            stack.peek().add(string);
                            status = STATUS_EXPECT_COMMA.index | STATUS_EXPECT_EXPRESSION_END.index;
                            continue;
                        }
                    } else if (hasStatus(status, STATUS_EXPECT_FILTER_COMPARISON.index)) {
                        PathType.PathFilter.FilterComparator comparator = reader.readComparator();
                        if (stack.peek(StackValue.TYPE_FILTER).valueAsFilter().index == 0) {
                            stack.peek().valueAsFilter().comparator = comparator;
                            stack.peek().valueAsFilter().index = 1;
                            status = STATUS_EXPECT_JSON_ROOT.index | STATUS_EXPECT_JSON_CURRENT.index | STATUS_EXPECT_PATH_POINT.index | STATUS_EXPECT_NUMBER.index | STATUS_EXPECT_EXPRESSION_START.index;
                            continue;
                        }
                    } else {
                        StringBuilder stringBuilder = reader.readStringBuilder(false, false);
                        if (!TokenReader.isHasQuotationMarks(stringBuilder) && hasStatus(status, STATUS_EXPECT_PATH_POINT.index)) {
                            PathType.PathPath pathPath = new PathType.PathPath(stringBuilder.toString());
                            if (stack.getTopValueType() == StackValue.TYPE_BASE_PATH) {
                                stack.peek().add(pathPath);
                                status = STATUS_EXPECT_POINT.index | STATUS_EXPECT_EXPRESSION_START.index | STATUS_EXPECT_END_DOCUMENT.index;
                            } else if (stack.getTopValueType() == StackValue.TYPE_EXPRESSION) {
                                stack.peek().add(pathPath);
                                status = STATUS_EXPECT_COMMA.index | STATUS_EXPECT_EXPRESSION_END.index;
                            } else if (stack.getTopValueType() == StackValue.TYPE_FILTER) {
                                stack.peek().add(pathPath);
                                status = STATUS_EXPECT_POINT.index | STATUS_EXPECT_EXPRESSION_START.index | STATUS_EXPECT_FILTER_COMPARISON.index | STATUS_EXPECT_FILTER_END.index;
                            }
                            continue;
                        } else if (TokenReader.isHasQuotationMarks(stringBuilder) && hasStatus(status, STATUS_EXPECT_FILTER_SINGLE_STRING.index)) {
                            TokenReader.deleteQuotationMarks(stringBuilder);
                            StackValue.Filter.FilterPart filterPart = stack.peek(StackValue.TYPE_FILTER).valueAsFilter().getCurrentPart();
                            filterPart.changeMode(PathType.PathFilter.PathFilterPart.FilterPartMode.SINGLE);
                            filterPart.part.add(stringBuilder.toString());

                            if (stack.peek(StackValue.TYPE_FILTER).valueAsFilter().index == 0)
                                status = STATUS_EXPECT_FILTER_COMPARISON.index | STATUS_EXPECT_FILTER_END.index;
                            else
                                status = STATUS_EXPECT_FILTER_END.index;
                            continue;
                        }
                    }
                    throw new PathParseException("Unexpected String", reader.reader.getErrorMessage());
                case EXPRESSION_START:
                    if (hasStatus(status, STATUS_EXPECT_EXPRESSION_START.index)) {
                        stack.push(StackValue.newExpression());
                        status = STATUS_EXPECT_FILTER_START.index | STATUS_EXPECT_COLON.index | STATUS_EXPECT_EXPRESSION_ELEMENT.index | STATUS_EXPECT_SYNTAX_ASTERISK.index;
                        continue;
                    }
                    throw new PathParseException("Unexpected [", reader.reader.getErrorMessage());
                case NUMBER:
                    if (hasStatus(status, STATUS_EXPECT_NUMBER.index)) {
                        int number = reader.readNumber().intValue();
                        if (stack.getTopValueType() == StackValue.TYPE_FILTER) {
                            stack.peek().add(number);
                            stack.peek().valueAsFilter().getCurrentPart().changeMode(PathType.PathFilter.PathFilterPart.FilterPartMode.SINGLE);
                            status = STATUS_EXPECT_FILTER_COMPARISON.index | STATUS_EXPECT_FILTER_END.index;
                            continue;
                        }
                    } else if (hasStatus(status, STATUS_EXPECT_EXPRESSION_ELEMENT.index)) {
                        int number = reader.readNumber().intValue();
                        if (stack.getTopValueType() == StackValue.TYPE_EXPRESSION) {
                            stack.peek().add(number);
                            stack.peek().valueAsExpression().isJustColon = false;
                            status = STATUS_EXPECT_COLON.index | STATUS_EXPECT_COMMA.index | STATUS_EXPECT_EXPRESSION_END.index;
                            continue;
                        }
                    }
                    throw new PathParseException("Unexpected number", reader.reader.getErrorMessage());
                case SPLIT_COLON: // :
                    if (hasStatus(status, STATUS_EXPECT_COLON.index)) {
                        stack.peek(StackValue.TYPE_EXPRESSION).valueAsExpression().mode = StackValue.Expression.ExpressionMode.INDEX;
                        if (stack.peek(StackValue.TYPE_EXPRESSION).valueAsExpression().isJustColon)
                            stack.peek(StackValue.TYPE_EXPRESSION).valueAsExpression().index.add(Integer.MIN_VALUE);
                        else
                            stack.peek(StackValue.TYPE_EXPRESSION).valueAsExpression().isJustColon = true;
                        status = STATUS_EXPECT_EXPRESSION_ELEMENT.index | STATUS_EXPECT_EXPRESSION_END.index | STATUS_EXPECT_COLON.index;
                        continue;
                    }
                    throw new PathParseException("Unexpected :", reader.reader.getErrorMessage());
                case SPLIT_COMMA: // ,
                    if (hasStatus(status, STATUS_EXPECT_COMMA.index)) {
                        if (stack.peek(StackValue.TYPE_EXPRESSION).valueAsExpression().mode == StackValue.Expression.ExpressionMode.ARRAY) {
                            status = STATUS_EXPECT_EXPRESSION_ELEMENT.index;
                            continue;
                        }
                    }
                    throw new PathParseException("Unexpected ,", reader.reader.getErrorMessage());
                case SYNTAX_ASTERISK: // *
                    if (hasStatus(status, STATUS_EXPECT_SYNTAX_ASTERISK.index)) {
                        if (stack.getTopValueType() == StackValue.TYPE_EXPRESSION) {
                            stack.peek(StackValue.TYPE_EXPRESSION).valueAsExpression().mode = StackValue.Expression.ExpressionMode.INDEX;
                            stack.peek(StackValue.TYPE_EXPRESSION).valueAsExpression().index.add(0);
                            stack.peek(StackValue.TYPE_EXPRESSION).valueAsExpression().index.add(Integer.MAX_VALUE);
                            status = STATUS_EXPECT_EXPRESSION_END.index;
                            continue;
                        }
                    }
                    throw new PathParseException("Unexpected *", reader.reader.getErrorMessage());
                case FILTER_START:
                    if (hasStatus(status, STATUS_EXPECT_FILTER_START.index)) {
                        stack.push(StackValue.newFilter());
                        status = STATUS_EXPECT_JSON_ROOT.index | STATUS_EXPECT_JSON_CURRENT.index | STATUS_EXPECT_PATH_POINT.index | STATUS_EXPECT_EXPRESSION_START.index | STATUS_EXPECT_NUMBER.index;
                        continue;
                    }
                    throw new PathParseException("Unexpected ?(", reader.reader.getErrorMessage());
                case FILTER_END:
                    if (hasStatus(status, STATUS_EXPECT_FILTER_END.index)) {
                        if (stack.getTopValueType() == StackValue.TYPE_FILTER) {
                            StackValue.Filter filter = stack.pop(StackValue.TYPE_FILTER).valueAsFilter();
                            if (stack.getTopValueType() == StackValue.TYPE_EXPRESSION) {
                                stack.peek(StackValue.TYPE_EXPRESSION).valueAsExpression().mode = StackValue.Expression.ExpressionMode.FILTER;
                                stack.peek(StackValue.TYPE_EXPRESSION).valueAsExpression().filter = filter;
                                status = STATUS_EXPECT_EXPRESSION_END.index;
                                continue;
                            }
                        }
                    }
                    throw new PathParseException("Unexpected )", reader.reader.getErrorMessage());
                case FILTER_COMPARISON:
                    if (hasStatus(status, STATUS_EXPECT_FILTER_COMPARISON.index)) {
                        PathType.PathFilter.FilterComparator comparator = reader.readComparator();
                        if (stack.peek(StackValue.TYPE_FILTER).valueAsFilter().index == 0) {
                            stack.peek().valueAsFilter().comparator = comparator;
                            stack.peek().valueAsFilter().index = 1;
                            status = STATUS_EXPECT_JSON_ROOT.index | STATUS_EXPECT_JSON_CURRENT.index | STATUS_EXPECT_PATH_POINT.index | STATUS_EXPECT_NUMBER.index | STATUS_EXPECT_FILTER_SINGLE_STRING.index | STATUS_EXPECT_EXPRESSION_START.index;
                            continue;
                        }
                    }
                    throw new PathParseException("Unexpected filter comparison", reader.reader.getErrorMessage());
                case EXPRESSION_END:
                    if (hasStatus(status, STATUS_EXPECT_EXPRESSION_END.index)) {
                        if (stack.getTopValueType() == StackValue.TYPE_EXPRESSION) {
                            Object pathPath = null;
                            StackValue.Expression expression = stack.pop(StackValue.TYPE_EXPRESSION).valueAsExpression();
                            if (expression.mode == StackValue.Expression.ExpressionMode.INDEX) {
                                if (expression.index.size() == 3)
                                    pathPath = new PathType.PathIndex((int) expression.index.get(0), (int) expression.index.get(1), (int) expression.index.get(2));
                                else if (expression.index.size() == 2)
                                    pathPath = new PathType.PathIndex((int) expression.index.get(0), (int) expression.index.get(1));
                                else if (expression.index.size() == 1)
                                    pathPath = new PathType.PathIndex((int) expression.index.get(0));
                            } else if (expression.mode == StackValue.Expression.ExpressionMode.ARRAY) {
                                if (expression.index.size() == 1 && expression.index.get(0) instanceof String)
                                    pathPath = new PathType.PathPath((String) expression.index.get(0));
                                else
                                    pathPath = new PathType.PathIndexArray(expression.index);
                            } else if (expression.mode == StackValue.Expression.ExpressionMode.FILTER)
                                pathPath = new PathType.PathFilter(expression.filter);

                            if (pathPath != null) {
                                stack.peek().add(pathPath);
                                if (stack.getTopValueType() == StackValue.TYPE_BASE_PATH)
                                    status = STATUS_EXPECT_POINT.index | STATUS_EXPECT_EXPRESSION_START.index | STATUS_EXPECT_END_DOCUMENT.index;
                                else if (stack.getTopValueType() == StackValue.TYPE_FILTER)
                                    status = STATUS_EXPECT_POINT.index | STATUS_EXPECT_EXPRESSION_START.index | STATUS_EXPECT_FILTER_END.index;
                                continue;
                            }
                        }
                    }
                    throw new PathParseException("Unexpected ]", reader.reader.getErrorMessage());
                case END_DOCUMENT:
                    if (hasStatus(status, STATUS_EXPECT_END_DOCUMENT.index)) {
                        return stack.pop(StackValue.TYPE_BASE_PATH).valueAsBasePath().paths;
                    }
                    throw new PathParseException("Unexpected EOF.", reader.reader.getErrorMessage());
            }
        }
    }
}
