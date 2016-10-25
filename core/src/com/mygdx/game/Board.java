package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import java.util.Vector;

public class Board {

    public  Sprite s;
    public Sprite sb;
    piece dragged;
    class piece{
        public int x;
        public int y;
        public int sx;
        public int ex;
        public int sy;
        public int ey;
        public Sprite sprite;
        public piece(int a, int b, Sprite s)
        {
            x = a;
            y = b;
            sprite = s;
            sx = a-(int)s.getWidth()/2;
            ex = a+(int)s.getWidth()/2;
            sy = b-(int)s.getHeight()/2;
            ey = b+(int)s.getHeight()/2;
        }
        boolean touched(int x, int y)
        {
            if(x <= ex && x >= sx && y >= sy && y <= ey)
            {
                return true;
            }
            return false;
        }

    }
    Vector<piece> pieces;
    int[][] positionMap;
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
        sb = createScaledSprite(ballb, SCALE_RATIO);
    }
    void add(int x, int y, int z)
    {
        piece p;
        if (z == 1) {
            p = new piece(x, y, s);
        }
        else
        {
            p = new piece(x, y, sb);
        }
        pieces.add(p);
        positionMap[x][y] = pieces.size() - 1;

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
        if (x != desx || y != desy) {
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
