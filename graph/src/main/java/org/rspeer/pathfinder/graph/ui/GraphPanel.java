package org.rspeer.pathfinder.graph.ui;

import org.rspeer.pathfinder.graph.model.hpa.HpaEdge;
import org.rspeer.pathfinder.graph.model.hpa.HpaGraph;
import org.rspeer.pathfinder.graph.model.hpa.HpaNode;
import org.rspeer.pathfinder.graph.model.rs.Position;
import org.rspeer.pathfinder.graph.model.rs.RegionFlags;
import org.rspeer.pathfinder.graph.service.MapImageService;
import org.rspeer.pathfinder.graph.service.RegionFlagsService;
import org.rspeer.pathfinder.graph.util.MapFlags;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

public class GraphPanel extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener, Runnable {

    private final Thread paintThread;
    private final RegionFlagsService regionFlagsService;
    private final MapImageService mapImageService;
    private final HpaGraph graph;
    private Position base = new Position(3200, 3200, 0);
    private int size = 8;
    private Point pressed;
    private boolean repainting;

    public GraphPanel(HpaGraph graph, RegionFlagsService regionFlagsService, MapImageService mapImageService) {
        this.regionFlagsService = regionFlagsService;
        this.mapImageService = mapImageService;
        this.graph = graph;
        regionFlagsService.loadAllIntoMemory();
        setPreferredSize(new Dimension(size * 128, size * 128));
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

        this.paintThread = new Thread(this);
        this.paintThread.start();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;
        drawClean(g, this::drawRegions);

        int tilesWidth = getWidth() / size;
        int tilesHeight = getHeight() / size;
        int wOccurences = (int) Math.ceil((double) getWidth() / ((double) getWidth() / tilesWidth * 64.0)) + 1;
        int hOccurences = (int) Math.ceil((double) getHeight() / ((double) getHeight() / tilesHeight * 64.0)) + 1;

        if (graph == null) return;
        List<HpaNode> nodes = graph.getLeafNodesIn(base, wOccurences, hOccurences, 64);
        for (HpaNode node : nodes) {
            Position key = node.getRoot().translate(-base.getX(), -base.getY());
            boolean blocked = MapFlags.isBlocked(regionFlagsService.getFlagFor(node.getRoot()));
            if (blocked) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.BLUE);
            }
            g.drawRect(key.getX() * size - 2, getHeight() - (key.getY()) * size - 2, 4, 4);
            for (HpaEdge edge : node.outgoing()) {
                HpaNode to = edge.getEnd();
                Position based = to.getRoot().translate(-base.getX(), -base.getY());
                g.drawLine(key.getX() * size - 2, getHeight() - (key.getY()) * size - 2,
                        based.getX() * size - 2, getHeight() - (based.getY()) * size - 2);
            }
        }
    }

    private void drawRegions(Graphics2D g) {
        g.setColor(Color.BLUE);

        int tilesWidth = getWidth() / size;
        int tilesHeight = getHeight() / size;
        int level = base.getLevel();
        int wOccurences = (int) Math.ceil((double) getWidth() / ((double) getWidth() / tilesWidth * 64.0)) + 1;
        int hOccurences = (int) Math.ceil((double) getHeight() / ((double) getHeight() / tilesHeight * 64.0)) + 1;
        int imageWidth = (int) (getWidth() / ((double) tilesWidth / 64d));
        int imageHeight = (int) (getHeight() / ((double) tilesHeight / 64d));
        for (int x = 0; x < wOccurences; x++) {
            for (int y = 0; y < hOccurences; y++) {
                Position draw = base.translate(64)
                        .translate(64 * x, 64 * y)
                        .translate(-base.getX(), -base.getY());
                g.setStroke(new BasicStroke(5));
                g.drawRect(draw.getX() * size, getHeight() - (draw.getY() + 64) * size, imageWidth, imageHeight);

                RegionFlags flags = regionFlagsService.getFor(base.translate(x * 64, y * 64));
                if (flags == null) continue;
                BufferedImage image = mapImageService.get(flags.getId(), level);
                if (image == null) continue;
                g.drawImage(image, draw.getX() * size, getHeight() - (draw.getY() + 64) * size, imageWidth, imageHeight, null);
            }
        }
    }

    private void drawClean(Graphics2D parent, Consumer<Graphics2D> drawer) {
        Graphics2D cleaned = (Graphics2D) parent.create();
        drawer.accept(cleaned);
        cleaned.dispose();
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        int dx = mouseEvent.getX() - pressed.x;
        int dy = mouseEvent.getY() - pressed.y;
        base = base.translate((int) (-dx * .95), (int) (dy * .95));
        pressed = mouseEvent.getPoint();
        queueRepaint();
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        pressed = mouseEvent.getPoint();
        int flag = (regionFlagsService.getFlagFor(base.translate(mouseEvent.getX() / size, mouseEvent.getY() / size)));
        System.out.println(Integer.toBinaryString(flag));
        queueRepaint();
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    private void queueRepaint() {
        this.repainting = true;
    }

    private boolean shouldRepaint() {
        return this.repainting;
    }

    @Override
    public void run() {
        while (isVisible()) {
            if (shouldRepaint()) {
                repaint();
                this.repainting = false;
            }
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        size = Math.max(1, Math.min(20, size - mouseWheelEvent.getWheelRotation()));
        queueRepaint();
    }
}
