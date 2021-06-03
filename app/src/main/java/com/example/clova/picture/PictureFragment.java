package com.example.clova.picture;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.clova.R;
import com.example.clova.picture.util.CanvasIO;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PictureFragment extends Fragment {

    private DrawCanvas drawCanvas;
    private FloatingActionButton fbPen;             //펜 모드 버튼
    private FloatingActionButton fbEraser;          //지우개 모드 버튼
    private FloatingActionButton fbSave;            //그림 저장 버튼
    private FloatingActionButton fbEraserAll;            //그림 모두 지우기
    private ConstraintLayout canvasContainer;       //캔버스 root view

    private final int PERMISSIONS_REQUEST_RESULT = 100;

    public PictureFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PictureFragment newInstance() {
        PictureFragment pictureFragment = new PictureFragment();
        return pictureFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_picture, container, false);

        //startActivity(new Intent(getActivity(), PictureActivity.class));
        canvasContainer = view.findViewById(R.id.lo_canvas);
        fbPen = view.findViewById(R.id.fb_pen);
        fbEraser = view.findViewById(R.id.fb_eraser);
        fbSave = view.findViewById(R.id.fb_save);
        fbEraserAll = view.findViewById(R.id.fb_eraer_All);
        drawCanvas = new DrawCanvas(getContext());

        canvasContainer.addView(drawCanvas);

        setOnClickListener();

        return view;
    }


    /**
     * OnClickListener Setting
     */
    private void setOnClickListener() {
        fbPen.setOnClickListener((v) -> {
            drawCanvas.changeTool(DrawCanvas.MODE_PEN);
        });

        fbEraser.setOnClickListener((v) -> {
            drawCanvas.changeTool(DrawCanvas.MODE_ERASER);
        });

        fbSave.setOnClickListener((v) -> {
            drawCanvas.invalidate();
            Bitmap saveBitmap = drawCanvas.getCurrentCanvas();
            Toast.makeText(getContext(),"저장", Toast.LENGTH_SHORT).show();
            CanvasIO.saveBitmapToJpg(saveBitmap, getContext());
        });


        fbEraserAll.setOnClickListener((v) -> {
            drawCanvas.init();
            //.loadDrawImage = CanvasIO.openBitmap(this);
            drawCanvas.invalidate();
        });
    }

    /**
     * Pen을 표현할 class입니다.
     */
    class Pen {
        public static final int STATE_START = 0;        //펜의 상태(움직임 시작)
        public static final int STATE_MOVE = 1;         //펜의 상태(움직이는 중)
        float x, y;                                     //펜의 좌표
        int moveStatus;                                 //현재 움직임 여부
        int color;                                      //펜 색
        int size;                                       //펜 두께

        public Pen(float x, float y, int moveStatus, int color, int size) {
            this.x = x;
            this.y = y;
            this.moveStatus = moveStatus;
            this.color = color;
            this.size = size;
        }

        /**
         * jhChoi - 201124
         * 현재 pen의 상태가 움직이는 상태인지 반환합니다.
         */
        public boolean isMove() {
            return moveStatus == STATE_MOVE;
        }
    }

    /**
     * 그림이 그려질 canvas view
     */
    class DrawCanvas extends View {
        public static final int MODE_PEN = 1;                     //모드 (펜)
        public static final int MODE_ERASER = 0;                  //모드 (지우개)
        final int PEN_SIZE = 4;                                   //펜 사이즈
        final int ERASER_SIZE = 40;                               //지우개 사이즈

        ArrayList<Pen> drawCommandList;                           //그리기 경로가 기록된 리스트
        Paint paint;                                              //펜
        Bitmap loadDrawImage;                                     //호출된 이전 그림
        int color;                                                //현재 펜 색상
        int size;                                                 //현재 펜 크기

        public DrawCanvas(Context context) {
            super(context);
            init();
        }

        public DrawCanvas(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public DrawCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        /**
         * 그리기에 필요한 요소를 초기화 합니다.
         */
        private void init() {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            drawCommandList = new ArrayList<>();
            loadDrawImage = null;
            color = Color.BLACK;
            size = PEN_SIZE;
        }

        /**
         * jhChoi - 201124
         * 현재까지 그린 그림을 Bitmap으로 반환합니다.
         */
        public Bitmap getCurrentCanvas() {
            Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            this.draw(canvas);
            return bitmap;
        }

        /**
         /**
         * jhChoi - 201124
         * Tool type을 (펜 or 지우개)로 변경합니다.
         */
        private void changeTool(int toolMode) {
            if (toolMode == MODE_PEN) {
                this.color = Color.BLACK;
                size = PEN_SIZE;
            } else {
                this.color = Color.WHITE;
                size = ERASER_SIZE;
            }
            paint.setColor(color);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);

            if (loadDrawImage != null) {
                canvas.drawBitmap(loadDrawImage, 0, 0, null);
            }

            for (int i = 0; i < drawCommandList.size(); i++) {
               Pen p = drawCommandList.get(i);
                paint.setColor(p.color);
                paint.setStrokeWidth(p.size);

                if (p.isMove()) {
                    Pen prevP = drawCommandList.get(i - 1);
                    canvas.drawLine(prevP.x, prevP.y, p.x, p.y, paint);
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            int action = e.getAction();
            int state = action == MotionEvent.ACTION_DOWN ? Pen.STATE_START : Pen.STATE_MOVE;
            drawCommandList.add(new Pen(e.getX(), e.getY(), state, color, size));
            invalidate();
            return true;
        }
    }
}