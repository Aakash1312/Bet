package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import static java.lang.System.*;

public class Board {

    public  Sprite s;
    public Sprite sb;
    class point{
        int x;
        int y;
        point(int a, int b)
        {
            x = a;
            y = b;
        }
        @Override
        public boolean equals(Object b)
        {
            if (b == null)
            {
                return false;
            }
            if (!point.class.isAssignableFrom(b.getClass()))
            {
                return false;
            }
            point a = (point) b;
            if (a.x == x && a.y == y)
            {
                return true;
            }
            return false;
        }
    }
    class piece{
        public int x;
        public int y;
        public int sx;
        public int ex;
        public int sy;
        public int ey;
        public cluster head;
        public Sprite sprite;
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        public piece(int a, int b, Sprite s)
        {
            x = a;
            y = b;
            sprite = s;
            sx = a-(int)s.getWidth()/2;
            ex = a+(int)s.getWidth()/2;
            sy = b-(int)s.getHeight()/2;
            ey = b+(int)s.getHeight()/2;
            head = new cluster();
            head.add(x,y);
        }
        boolean touched(float x, float y)
        {
            x = (10 * (x - w / 5) / h);
            y = (10 * (y) / h);
            if(x <= ex && x >= sx && y >= sy && y <= ey)
            {
                return true;
            }
            return false;
        }

    }
    Vector<piece> pieces;
    int[][] positionMap;
    class cluster{
        HashSet<point> points;
        HashSet<point> actualPoints;
        public int liberty;
        int size;
        public cluster head;
        public boolean isBlack;
        public cluster(){
            points = new HashSet<point>();
            actualPoints = new HashSet<point>();
            size = 1;
            head = this;
            liberty = 0;
        };
        public cluster union(cluster B)
        {
            if (head.size > B.head.size)
            {
                head.points.addAll(B.head.points);
                head.actualPoints.addAll(B.head.actualPoints);
                head.liberty = head.points.size();
                head.size += B.head.size;
                B.head = head;
                return head;
            }
            else
            {
                B.head.points.addAll(head.points);
                B.head.actualPoints.addAll(head.actualPoints);
                head.liberty = B.head.points.size();
                B.head.size += head.size;
                head = B.head;
                return B.head;
            }
        }
        public void add(int x, int y)
        {
            actualPoints.add(new point(x ,y));
            if (x != 0) {
                head.points.add(new point(x-1, y));
            }
            if (y != 0) {
                head.points.add(new point(x, y-1));
            }
            if (x != 9) {
                head.points.add(new point(x+1, y));
            }
            if (y != 9) {
                head.points.add(new point(x, y+1));
            }
            head.liberty = head.points.size();
            head.size++;
        }

        public void removeC(int x, int y)
        {
            Iterator<point> i = head.points.iterator();
            while (i.hasNext())
            {
                point p = i.next();
                if (p.x == x && p.y == y)
                {
                    i.remove();
                    break;
                }
            }
            head.liberty = head.points.size();
            head.size = head.size - 1;
            if (head.liberty == 0)
            {
                delete();
            }
        }
        public void delete()
        {
            liberalize(head.actualPoints);
            for (point point : head.actualPoints) {
                remove(point.x, point.y);
            }

        }
    }

    Board(int h)
    {
        pieces = new Vector<piece>();
        positionMap = new int[10][10];
        for (int i = 0 ; i < 10; i++)
        {
            for (int j = 0 ; j<10 ;j++) {
                positionMap[i][j] = 1000;
            }
        }

        Texture ball = new Texture(Gdx.files.internal("sprite.png"));
        Texture ballb = new Texture(Gdx.files.internal("spriteb.png"));
        float SCALE_RATIO = 10*(ball.getHeight())/(float)h;
        s = createScaledSprite(ball, SCALE_RATIO);
        sb = createScaledSprite(ballb, (float) (SCALE_RATIO*(0.8)));
    }
    public void liberalize(HashSet<point> points)
    {
        for (point point : points) {
            int x = point.x;
            int y = point.y;
            System.out.println("liberal " + x+" "+y);
            if (x != 0) {
                if (positionMap[x-1][y] != 1000) {
                    pieces.get(positionMap[x-1][y]).head.points.add(new point(x, y));
                    pieces.get(positionMap[x-1][y]).head.liberty = pieces.get(positionMap[x-1][y]).head.points.size();
                    pieces.get(positionMap[x-1][y]).head.size++;
                }
            }
            if (y != 0) {
                if (positionMap[x][y-1] != 1000) {
                    pieces.get(positionMap[x][y-1]).head.points.add(new point(x, y));
                    pieces.get(positionMap[x][y-1]).head.liberty = pieces.get(positionMap[x][y-1]).head.points.size();
                    pieces.get(positionMap[x][y-1]).head.size++;
                }
            }
            if (x != 9) {
                if (positionMap[x+1][y] != 1000) {
                    pieces.get(positionMap[x+1][y]).head.points.add(new point(x, y));
                    pieces.get(positionMap[x+1][y]).head.liberty = pieces.get(positionMap[x+1][y]).head.points.size();
                    pieces.get(positionMap[x+1][y]).head.size++;
                }
            }
            if (y != 9) {
                if (positionMap[x][y+1] != 1000) {
                    pieces.get(positionMap[x][y+1]).head.points.add(new point(x, y));
                    pieces.get(positionMap[x][y+1]).head.liberty = pieces.get(positionMap[x][y+1]).head.points.size();
                    pieces.get(positionMap[x][y+1]).head.size++;
                }
            }
        }
    }
    void clusterize(int x, int y ,int z, piece p)
    {
        if (z == 1) {
            if (x != 0) {
                if (positionMap[x - 1][y] != 1000) {
                    if (!pieces.get(positionMap[x - 1][y]).head.isBlack) {
                        p.head.union(pieces.get(positionMap[x - 1][y]).head);
                    }
                    p.head.removeC(x - 1, y);
                    pieces.get(positionMap[x - 1][y]).head.removeC(x,y);

                }
            }
            if (y != 0) {
                if (positionMap[x][y-1] != 1000) {
                    if (!pieces.get(positionMap[x][y-1]).head.isBlack) {
                        p.head.union(pieces.get(positionMap[x][y-1]).head);
                    }
                    p.head.removeC(x,y-1);
                    pieces.get(positionMap[x][y-1]).head.removeC(x,y);
                }
            }
            if (y != 9) {
                if (positionMap[x][y+1] != 1000) {
                    if (!pieces.get(positionMap[x][y+1]).head.isBlack) {
                        p.head.union(pieces.get(positionMap[x][y+1]).head);
                    }
                    p.head.removeC(x, y + 1);
                    pieces.get(positionMap[x][y+1]).head.removeC(x, y);
                }
            }
            if (x != 9) {
                if (positionMap[x + 1][y] != 1000) {
                    if (!pieces.get(positionMap[x + 1][y]).head.isBlack) {
                        p.head.union(pieces.get(positionMap[x + 1][y]).head);
                    }
                    p.head.removeC(x + 1, y);
                    pieces.get(positionMap[x + 1][y]).head.removeC(x, y);
                }
            }
        }
        else
        {
            if (x != 0) {
                if (positionMap[x - 1][y] != 1000) {
                    if (pieces.get(positionMap[x - 1][y]).head.isBlack) {
                        p.head.union(pieces.get(positionMap[x - 1][y]).head);
                    }
                    p.head.removeC(x - 1, y);
                    pieces.get(positionMap[x - 1][y]).head.removeC(x, y);
                }
            }
            if (y != 0) {
                if (positionMap[x][y-1] != 1000) {
                    if (pieces.get(positionMap[x][y-1]).head.isBlack) {
                        p.head.union(pieces.get(positionMap[x][y-1]).head);
                    }
                    p.head.removeC(x, y - 1);
                    pieces.get(positionMap[x][y-1]).head.removeC(x, y);
                }
            }
            if (y != 9) {
                if (positionMap[x][y+1] != 1000) {
                    if (pieces.get(positionMap[x][y+1]).head.isBlack) {
                        p.head.union(pieces.get(positionMap[x][y+1]).head);
                    }
                    p.head.removeC(x, y + 1);
                    pieces.get(positionMap[x][y+1]).head.removeC(x, y);
                }
            }
            if (x != 9) {
                if (positionMap[x + 1][y] != 1000) {
                    if (pieces.get(positionMap[x + 1][y]).head.isBlack) {
                        p.head.union(pieces.get(positionMap[x + 1][y]).head);
                    }
                    p.head.removeC(x + 1, y);
                    pieces.get(positionMap[x + 1][y]).head.removeC(x, y);
                }
            }
        }
    }
    void add(int x, int y, int z)
    {
        piece p;
        if (z == 1) {
            p = new piece(x, y, s);
            p.head.isBlack = false;
        }
        else
        {
            p = new piece(x, y, sb);
            p.head.isBlack = true;
        }
        clusterize(x, y, z, p);
        if (p.head.liberty != 0) {
            pieces.add(p);
            positionMap[x][y] = pieces.size() - 1;
        }

    }
    void remove(int x, int y) {
        if (positionMap[x][y] != 1000) {
            pieces.remove(positionMap[x][y]);
            int i = positionMap[x][y];
            while (i<pieces.size())
            {
                positionMap[pieces.get(i).x][pieces.get(i).y]--;
                i++;
            }
            positionMap[x][y] = 1000;

        }
    }
    void move(int x,int y, int desx, int desy) {
        if ((x != desx || y != desy) && (positionMap[desx][desy] == 1000)) {
            pieces.get(positionMap[x][y]).x = desx;
            pieces.get(positionMap[x][y]).y = desy;
            positionMap[desx][desy] = positionMap[x][y];
            positionMap[x][y] = 1000;
        }
    }
    public Sprite createScaledSprite(Texture texture, float SCALE_RATIO) {
        Sprite sprite = new Sprite(texture);
        sprite.getTexture().setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear);
        sprite.setSize(sprite.getWidth() / SCALE_RATIO,
                sprite.getHeight() / SCALE_RATIO);
        return sprite;
    }

}
