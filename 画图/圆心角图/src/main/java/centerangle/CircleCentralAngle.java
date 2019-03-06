package centerangle;

import com.google.common.base.Strings;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

public class CircleCentralAngle {

    enum LineDecoration {
        NONE,
        ARROW,
    }

    enum LineStyle {
        DASH,
        SOLID,
    }

    private static final int FONT_SIZE = 15;
    private static final int TEXT_MARGIN = 20;// 文本与弦端的间隔
    private static final Font FONT = new Font("Serif", Font.BOLD, FONT_SIZE);
    private static final int ARROW_ANGLE = 30; // 箭头与弦的夹角
    private static final int ARROW_LENGTH_RATIO = 5;// 箭头的长度=ARROW_LENGTH_RATIO*lienWidth
    private final Graphics2D graphics2D;
    private final int radius;
    private int x;
    private int y;
    private int cooX;
    private int cooY;
    private final int diameter;
    private double baseAngle = 0;
    private float lienWidth = 1;
    private LineStyle lineStyle = LineStyle.SOLID;
    private LineDecoration lineDecoration = LineDecoration.NONE;

    /**
     * 初始化
     * @param graphics2D 图形对象
     * @param radius 圆半径
     */
    public CircleCentralAngle(final Graphics2D graphics2D, final int radius) {
        this.graphics2D = graphics2D;
        this.radius = radius;
        this.x = radius;
        this.y = radius;
        this.cooX = 0;
        this.cooY = 0;
        this.diameter = 2 * radius;
    }

    /**
     * 画基准弦
     *
     * @param color
     * @param length
     * @param angle
     *            弦角度
     * @param text
     */
    public void paintBaseLine(final Color color, final double length, final double angle, final String text) {
        this.graphics2D.setColor(color);
        this.baseAngle = angle;
        final double radians = Math.toRadians(angle);
        final double endX = this.x + Math.cos(radians) * length;
        final double endY = this.y - Math.sin(radians) * length;
        this.line(endX, endY, angle);
        this.text(text, length, radians);
    }

    public void paintCenterPoint(final Color color, final int pointRadius) {
        this.graphics2D.setColor(color);
        final Shape point =
                new Ellipse2D.Double(this.cooX + this.radius - pointRadius, this.cooY + this.radius - pointRadius, pointRadius * 2, pointRadius * 2);
        this.graphics2D.fill(point);
    }

    public void paintCircle(final Color color) {
        this.graphics2D.setColor(color);
        final Shape circle = new Ellipse2D.Double(this.cooX, this.cooY, this.diameter, this.diameter);
        this.graphics2D.draw(circle);
    }

    /**
     * 画相对弦
     *
     * @param color
     * @param length
     * @param centerAngle
     *            与基准弦的圆心角
     * @param text
     */
    public void paintRelativeLine(final Color color, final double length, final double centerAngle, final String text) {
        final double radians = Math.toRadians(centerAngle + this.baseAngle);
        this.graphics2D.setColor(color);
        final double endX = this.x + Math.cos(radians) * length;
        final double endY = this.y - Math.sin(radians) * length;
        this.line(endX, endY, centerAngle + this.baseAngle);
        this.text(text, length, radians);
    }

    public void setLineDecoration(final LineDecoration lineDecoration) {
        this.lineDecoration = lineDecoration;
        this.refreshStroke();
    }

    public void setLineStyle(final LineStyle lineStyle) {
        this.lineStyle = lineStyle;
        this.refreshStroke();
    }

    public void setLineWidth(final float lienWidth) {
        this.lienWidth = lienWidth;
        this.refreshStroke();
    }

    public void setX(final int x) {
        this.cooX = x - this.radius;
        this.x = x;
    }

    public void setY(final int y) {
        this.cooY = y - this.radius;
        this.y = y;
    }

    private void line(final double endX, final double endY, final double angle) {
        final Shape line = new Line2D.Double(this.x, this.y, endX, endY);
        this.graphics2D.draw(line);
        if (this.lineDecoration == LineDecoration.ARROW) {
            final int arrowLength = (int) (ARROW_LENGTH_RATIO * this.lienWidth);
            final Stroke stroke = this.graphics2D.getStroke();
            this.graphics2D.setStroke(new BasicStroke(1));
            final Path2D.Float path = new Path2D.Float();
            final double compensateX = arrowLength * Math.cos(Math.toRadians(ARROW_ANGLE)) * Math.cos(Math.toRadians(angle));
            final double compensateY = arrowLength * Math.cos(Math.toRadians(ARROW_ANGLE)) * Math.sin(Math.toRadians(angle));
            path.moveTo(endX + compensateX, endY - compensateY);
            final double x1 = endX - Math.cos(Math.toRadians(angle - ARROW_ANGLE)) * arrowLength;
            final double y1 = endY + Math.sin(Math.toRadians(angle - ARROW_ANGLE)) * arrowLength;
            path.lineTo(x1 + compensateX, y1 - compensateY);
            final double x2 = endX - Math.cos(Math.toRadians(angle + ARROW_ANGLE)) * arrowLength;
            final double y2 = endY + Math.sin(Math.toRadians(angle + ARROW_ANGLE)) * arrowLength;
            path.lineTo(x2 + compensateX, y2 - compensateY);
            this.graphics2D.fill(path);
            this.graphics2D.setStroke(stroke);
        }
    }

    private void refreshStroke() {
        BasicStroke stroke;
        if (this.lineStyle == LineStyle.DASH) {
            final float[] dash = { this.lienWidth * 2, 0, 0.5f };
            stroke = new BasicStroke(this.lienWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, dash, 0);
        }
        else {
            stroke = new BasicStroke(this.lienWidth);
        }
        this.graphics2D.setStroke(stroke);
    }

    private void text(final String text, final double stringLength, final double radians) {
        if (!Strings.isNullOrEmpty(text)) {
            final double textWidth = this.textSize(text)[0];
            final double textHeight = this.textSize(text)[1];
            final double rectangleCenterX = this.x + Math.cos(radians) * (stringLength + TEXT_MARGIN);
            final double rectangleCenterY = this.y + Math.sin(radians + Math.PI) * (stringLength + TEXT_MARGIN);
            final double rectangleCooX = rectangleCenterX - textWidth / 2;
            final double rectangleCooY = rectangleCenterY + textHeight / 2;
            this.graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            this.graphics2D.setFont(FONT);
            this.graphics2D.drawString(text, (float) rectangleCooX, (float) rectangleCooY);
        }
    }

    private double[] textSize(final String text) {
        final FontRenderContext context = this.graphics2D.getFontRenderContext();
        final Rectangle2D bound = FONT.getStringBounds(text, context);
        return new double[] { bound.getWidth(), bound.getHeight() };
    }
}
