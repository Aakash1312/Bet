package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class MyGdxGame extends ApplicationAdapter implements InputProcessor {
		private SpriteBatch batch;
        private int w;
		private int h;
        private float currentx;
        private float currenty;
        private int perx;
        private int pery;
        private TextureRegion backTile;
		private TextureRegion backTile2;
        private TextureRegion testTile;
        ParticleEffect effect;
        private Sprite container;
        private Sprite containerb;
        private int created;
        private int validate;
        private float x;
        private float y;
        private int draggged = 0;
        private boolean moved = false;
        private int de = 0;
        class TouchInfo {
                public float touchX = 0;
                public float touchY = 0;
                public boolean touched = false;
            }

        ShaderProgram shader;
        Board board;
		private Map<Integer,TouchInfo> touches = new HashMap<Integer,TouchInfo>();
	public Sprite createScaledSprite(Texture texture, float SCALE_RATIO) {
		Sprite sprite = new Sprite(texture);
		sprite.getTexture().setFilter(Texture.TextureFilter.Linear,
				Texture.TextureFilter.Linear);
		sprite.setSize(sprite.getWidth() / SCALE_RATIO,
				sprite.getHeight() / SCALE_RATIO);
		return sprite;
	}
		@Override
		public void create() {
            Texture tex = new Texture(Gdx.files.internal("sprite.png"));
            Texture texb = new Texture(Gdx.files.internal("spriteb.png"));
            w = Gdx.graphics.getWidth();
            h = Gdx.graphics.getHeight();
			batch = new SpriteBatch();
            board = new Board(h);
            ShaderProgram.pedantic =false;
            //Shaders
            String vertexShader = "attribute vec4 a_color;\n" +
                    "attribute vec3 a_position;\n" +
                    "attribute vec2 a_texCoord0;\n" +
                    "\n" +
                    "uniform mat4 u_projTrans;\n" +
                    "\n" +
                    "varying vec4 v_color;\n" +
                    "varying vec2 v_texCoord0;\n" +
                    "\n" +
                    "void main() {\n" +
                    "\tv_color = a_color;\n" +
                    "\tv_texCoord0 = a_texCoord0;\n" +
                    "\tgl_Position =  u_projTrans * vec4(a_position, 1.);\n" +
                    "}";
            String fragmentShader = "varying vec4 v_color;\n" +
                    "varying vec2 v_texCoord0;\n" +
                    "\n" +
                    "uniform vec2 u_resolution;\n" +
                    "uniform vec2 u_sprite;\n"+
                    "uniform sampler2D u_sampler2D;\n" +
                    "uniform int apply;"+
                    "\n" +
                    "const float outerRadius = .15, innerRadius = .05, intensity = .6;uniform float ig;\n" +
                    "\n" +
                    "void main() {\n" +
                    "\tvec4 color = texture2D(u_sampler2D, v_texCoord0);//*v_color\n" +
//                    "vec2 relativePosition = gl_FragCoord.xy / u_resolution - u_sprite.xy / u_resolution;\n"+
//                    "relativePosition.x *= u_resolution.x / u_resolution.y;" +
//                    "float leng = length(relativePosition);\n"+
////                    "if(leng>ig&&(leng<(ig+0.05))&&((gl_FragCoord.x+gl_FragCoord.y - 2.*(floor((gl_FragCoord.x+gl_FragCoord.y)/2.)))==1.0)){\n"+
////                    "if(leng>ig&&(leng<(ig+0.05))&&((100.*gl_FragCoord.x - (10.)*(floor((100.*gl_FragCoord.x)/(10.)))))<7.&&((100.*gl_FragCoord.y - (10.)*(floor((100.*gl_FragCoord.y)/(10.)))))>3.){\n"+
////                    "if(leng>ig&&(leng<(ig+0.05))&&(color.r > color.g||color.g>color.b)){\n"+
//                    "if(leng>ig&&(leng<(ig+0.05))){\n"+
//                    "if((sin((gl_FragCoord.x-u_sprite.x)*(gl_FragCoord.y-u_sprite.y))+cos((gl_FragCoord.x-u_sprite.x)/(gl_FragCoord.y-u_sprite.y)))<0.05){"+
//                    "float vig = smoothstep(0.3,0.1,leng);"+
//                    "color.rgb = mix(color.rgb,color.rgb*(vec3(0.9,0.1,0.1))*vig,0.9);}}\n"+
                    "float len = 0.0;\n"+
                    "if(apply == 1){"+
                    "for(int i =0 ;i<10;i++){\n"+
                    "vec2 hor = vec2("+Integer.toString(w/5+h/20)+"+("+Integer.toString(h/10)+"*i),u_sprite.y);\n"+
                    "\tvec2 relativePosition = gl_FragCoord.xy / u_resolution - hor.xy / u_resolution;\n" +
                    "\trelativePosition.x *= u_resolution.x / u_resolution.y;\n" +
                    "\tlen += 1./length(relativePosition);}\n" +
                    "for(int i =0 ;i<10;i++){\n"+
                    "vec2 hor = vec2(u_sprite.x,"+Integer.toString(h/20)+"+("+Integer.toString(h/10)+")*i);\n"+
                    "\tvec2 relativePosition = gl_FragCoord.xy / u_resolution - hor.xy / u_resolution;\n" +
                    "\trelativePosition.x *= u_resolution.x / u_resolution.y;\n" +
                    "\tlen += 1./length(relativePosition);}\n" +                    "\tlen = 10./len;\n" +
                    "\tfloat vignette = smoothstep(outerRadius, innerRadius, len);\n" +
                    "\tcolor.rgb = mix(color.rgb, color.rgb * vignette * vec3(0.9,0.2,0.2), intensity);}\n" +
                    "\n" +
                    "\tgl_FragColor = color;\n" +
                    "}";
            //&&(((gl_FragCoord.x - u_sprite.x) - (9.*(((gl_FragCoord.y-u_sprite.y))/10.)))<7.)
            shader = new ShaderProgram(vertexShader, fragmentShader);
            if (!shader.isCompiled())
            {
                System.out.println(shader.getLog());
            }
            batch.setShader(shader);
            shader.begin();
            shader.setUniformi("apply", 0);
            shader.setUniformf("u_resolution", w, h);
            shader.end();
			float SCALE_RATIO = 10*(tex.getHeight())/(float)h;
            container = createScaledSprite(tex, SCALE_RATIO*0.5f);
            containerb = createScaledSprite(texb, SCALE_RATIO*0.4f);
            Texture background = new Texture(Gdx.files.internal("wood.png"));
            backTile = new TextureRegion(background,0,0,h/10,h/10);
			Texture background2 = new Texture(Gdx.files.internal("whiteWood.jpg"));
			backTile2 = new TextureRegion(background2,0,0,h/10,h/10);
            testTile = new TextureRegion(background2,0,0,h/10,h/10);
            Gdx.input.setInputProcessor(this);
			for(int i = 0; i < 1; i++){
				touches.put(i, new TouchInfo());
			}
            effect = new ParticleEffect();
            effect.load(Gdx.files.internal("firebeam.p"), Gdx.files.internal(""));
            effect.setPosition(w / 2, h / 2);
            effect.start();


		}

		@Override
		public void dispose() {
			batch.dispose();
		}


		@Override
		public void render() {
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


			batch.begin();
            //Container
            container.setPosition(w-container.getWidth(),h/2-container.getHeight()/2);
            container.draw(batch);
            containerb.setPosition(containerb.getWidth()/2,h/2-containerb.getHeight()/2);
            containerb.draw(batch);            //Board tiles
            for(int i =0 ;i<10;i++){
                for (int j=0 ;j<10;j++)
                {
                    float a = w/5+i*backTile.getRegionWidth();
                    float b = j*backTile.getRegionHeight();
					if((i+j)%2==0) {
						batch.draw(backTile, a, b);
					}
					else {
					batch.draw(backTile2,a,b);}
                }
            }
            if (validate==1)
            {
//                piece a = new piece((int)(10 * (x - w/5) / h),(int)(10 * (y) / h),sprite);
                board.add((int)(10 * (x - w/5) / h),(int)(10 * (y) / h), 1);
                de++;
                if(de%4==0)
                {
                    board.remove(0,5);
                }
//                piecePositions[a.x][a.y] = 1;
                validate =0;
            }
			for(int i = 0; i < 1; i++){
				if(touches.get(i).touched)
                    x = touches.get(i).touchX;
                    y = touches.get(i).touchY;

			}

            shader.begin();
            //Code to handle new sprite creation
            if (x > (w-container.getWidth()) && x<w && (y>(h/2-container.getHeight()/2))&&(y<(h/2+container.getHeight()/2))){
                created = 1;
            }
            if(created==1 )
            {
//                sprite.setPosition(x - sprite.getWidth() / 2, h - y - sprite.getHeight() / 2);
//                sprite.draw(batch);
            }
            if(draggged == 1) {
                if ((int) (10 * (x - w / 5) / h)<10&&(int) (10 * (y) / h)<10&&(int) (10 * (y) / h)>-1&&(int) (10 * (x - w / 5) / h)>-1) {
                    if (board.positionMap[(int) (10 * (x - w / 5) / h)][(int) (10 * (y) / h)] != 1000 || moved) {
                        if (!moved) {
                            currentx = x;
                            currenty = y;
                            perx = (int) (10 * (x - w / 5) / h);
                            pery = (int) (10 * (y) / h);
                        }
                        moved = true;
                        if (x < (currentx + board.pieces.get(board.positionMap[perx][pery]).sprite.getWidth() / 2) && x > (currentx - board.pieces.get(board.positionMap[perx][pery]).sprite.getWidth() / 2) && y < (currenty + board.pieces.get(board.positionMap[perx][pery]).sprite.getHeight() / 2) && y > (currenty - board.pieces.get(board.positionMap[perx][pery]).sprite.getHeight() / 2)) {
                            board.pieces.get(board.positionMap[perx][pery]).sprite.setPosition(x - board.pieces.get(board.positionMap[perx][pery]).sprite.getWidth() / 2, h - y - board.pieces.get(board.positionMap[perx][pery]).sprite.getHeight() / 2);
                            currentx = x;
                            currenty = y;
                            shader.setUniformf("u_sprite", w / 5 + ((int) (10 * (x - w / 5) / h)) * h / 10 + h / 20, h - h / 20 - ((int) (10 * y / h)) * h / 10);
                            shader.setUniformi("apply", 1);
                        } else {
                            shader.setUniformi("apply", 0);
                        }
                    }
                }
//                if (x < (currentx + sprite.getWidth() / 2) && x > (currentx - sprite.getWidth() / 2) && y < (currenty + sprite.getHeight() / 2) && y > (currenty - sprite.getHeight() / 2)) {
//                    sprite.setPosition(x - sprite.getWidth() / 2, h - y - sprite.getHeight() / 2);
//                    currentx = x;
//                    currenty = y;
//                    shader.setUniformf("u_sprite", w / 5 + ((int) (10 * (x - w / 5) / h)) * h / 10 + h / 20, h - h / 20 - ((int) (10 * y / h)) * h / 10);
//                    shader.setUniformi("apply", 1);
//                } else {
//                    shader.setUniformi("apply", 0);
//                }

//                if (x < (currentx*h/10+w/5 + sprite.getWidth() / 2) && x > (currentx*h/10+w/5 - sprite.getWidth() / 2) && y < (currenty*h/10 + sprite.getHeight() / 2) && y > (currenty*h/10 - sprite.getHeight() / 2)) {
//                    sprite.setPosition(x - sprite.getWidth() / 2, h - y - sprite.getHeight() / 2);
//                    currentx = x;
//                    currenty = y;
//                    shader.setUniformf("u_sprite", w / 5 + ((int) (10 * (x - w / 5) / h)) * h / 10 + h / 20, h - h / 20 - ((int) (10 * y / h)) * h / 10);
//                    shader.setUniformi("apply", 1);
//                } else {
//                    shader.setUniformi("apply", 0);
//                }
            }
            else
            {
                shader.setUniformi("apply", 0);
                if ((int)(10 * (x - w/5) / h)>-1&&(int)(10 * (x - w/5) / h)<10&&(int)(10 * (y) / h)>-1&&(int)(10 * (y) / h)<10) {
                    if (board.positionMap[(int) (10 * (x - w / 5) / h)][(int) (10 * (y) / h)] == 1) {
                        currentx = perx = (int) (10 * (x - w / 5) / h);
                        currenty = pery = (int) (10 * (y) / h);
                    }
                }
            }
//            sprite.draw(batch);
            for(int i =0 ; i < board.pieces.size();i++)
            {
                board.pieces.get(i).sprite.setPosition((board.pieces.get(i).x)*h/10+w/5,h-((board.pieces.get(i).y +1)*h/10));
                board.pieces.get(i).sprite.draw(batch);
            }
//            shader.setUniformi("apply",0);
			batch.end();
            shader.end();
		}

		@Override
		public void resize(int width, int height) {
		}

		@Override
		public void pause() {
		}

		@Override
		public void resume() {
		}

		@Override
		public boolean keyDown(int keycode) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean keyTyped(char character) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			if(pointer < 1){
				touches.get(pointer).touchX = screenX;
				touches.get(pointer).touchY = screenY;
				touches.get(pointer).touched = true;
			}
			return true;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			if(pointer < 1){
				touches.get(pointer).touchX = 0;
				touches.get(pointer).touchY = 0;
				touches.get(pointer).touched = false;
                if(created == 1){
                    created = 0;
                validate = 1;}
                draggged = 0;
                if (moved)
                {
                    moved = false;
                    board.move(perx,pery,(int) (10 * (currentx - w / 5) / h),(int) (10 * (currenty) / h));
                }
			}
			return true;
		}

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
            if(pointer < 1){
                touches.get(pointer).touchX = screenX;
                touches.get(pointer).touchY = screenY;
                touches.get(pointer).touched = true;
                draggged = 1;
            }
            return true;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean scrolled(int amount) {
			// TODO Auto-generated method stub
			return false;
		}

}
