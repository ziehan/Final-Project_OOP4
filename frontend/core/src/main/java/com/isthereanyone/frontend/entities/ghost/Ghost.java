package com.isthereanyone.frontend.entities.ghost;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.ghost.strategies.*;
import com.isthereanyone.frontend.managers.MyAssetManager;
import com.isthereanyone.frontend.observer.GameObserver;

public class Ghost implements GameObserver {
    private Vector2 position;
    // Hapus variable Texture texture; ganti dengan animasi

    private GhostStrategy currentStrategy;

    public Vector2 investigationTarget = null;
    private float speedMultiplier = 1.0f;
    private float baseSpeed = 100f;

    private Array<Vector2> patrolPoints;

    // --- ANIMATION VARIABLES ---
    private Animation<TextureRegion> walkDown, walkUp, walkLeft, walkRight;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime = 0f;
    private Vector2 lastPosition = new Vector2(); // Untuk cek arah gerak

    // Warna Hantu (Merah Transparan)
    private final Color ghostColor = new Color(1f, 0.2f, 0.2f, 0.85f);

    public Ghost(float x, float y) {
        this.position = new Vector2(x, y);
        this.lastPosition.set(x, y);

        // 1. SETUP ANIMASI (Sama seperti Player)
        Texture sheet = MyAssetManager.getInstance().get("she.png");
        TextureRegion[][] tmp = TextureRegion.split(sheet, 32, 32);

        walkDown  = new Animation<>(0.2f, tmp[0]);
        walkUp    = new Animation<>(0.2f, tmp[1]);
        walkLeft  = new Animation<>(0.2f, tmp[2]);
        walkRight = new Animation<>(0.2f, tmp[3]);

        currentAnimation = walkDown; // Default menghadap bawah

        this.currentStrategy = new PatrolStrategy();
    }

    public void setPatrolPoints(Array<Vector2> points) {
        this.patrolPoints = points;
        this.currentStrategy = new WaypointStrategy(this.patrolPoints);
    }

    public void revertToPatrol() {
        if (patrolPoints != null) {
            this.currentStrategy = new WaypointStrategy(this.patrolPoints);
        } else {
            this.currentStrategy = new PatrolStrategy();
        }
    }

    public void update(Player player, float delta) {
        // Simpan posisi sebelum bergerak
        lastPosition.set(position.x, position.y);

        // Gerakkan hantu pakai strategy
        currentStrategy.executeBehavior(this, player, delta);

        // --- UPDATE ANIMASI BERDASARKAN GERAKAN ---
        Vector2 movement = new Vector2(position.x - lastPosition.x, position.y - lastPosition.y);

        // Cek arah mana yang dominan (Horizontal atau Vertikal)
        if (Math.abs(movement.x) > Math.abs(movement.y)) {
            if (movement.x > 0) currentAnimation = walkRight;
            else if (movement.x < 0) currentAnimation = walkLeft;
        } else if (Math.abs(movement.y) > 0.1f) { // Pakai threshold dikit biar gak flicker
            if (movement.y > 0) currentAnimation = walkUp;
            else if (movement.y < 0) currentAnimation = walkDown;
        }
        // Kalau diam, dia akan tetap pakai animasi terakhirnya.
    }

    public void render(SpriteBatch batch) {
        // 1. Ubah kuas jadi MERAH
        batch.setColor(ghostColor);

        // 2. Ambil frame animasi saat ini
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        // 3. Gambar frame tersebut
        batch.draw(currentFrame, position.x, position.y);

        // 4. PENTING: Kembalikan kuas jadi PUTIH (Normal)
        // Agar tidak menular ke gambar lain setelahnya
        batch.setColor(Color.WHITE);
    }

    public void setStrategy(GhostStrategy strategy) {
        this.currentStrategy = strategy;
    }

    public Vector2 getPosition() { return position; }
    public void setPosition(float x, float y) { this.position.set(x, y); }
    public float getSpeedMultiplier() { return speedMultiplier; }

    public float getSpeed() {
        return baseSpeed * speedMultiplier;
    }

    public void updateAnimationTime(float delta) {
        stateTime += delta;
    }

    // --- OBSERVER ---

    @Override
    public void onTaskCompleted(int totalTasksFinished) {
        speedMultiplier += 0.2f;
    }

    @Override
    public void onAllTasksCompleted() {
        speedMultiplier = 2.5f;
        setStrategy(new ChaseStrategy());
    }

    @Override
    public void onSoundEmitted(float x, float y, float radius) {
        float dist = position.dst(x, y);
        if (dist <= radius && !(currentStrategy instanceof ChaseStrategy)) {
            System.out.println("GHOST: Suara apa itu? (Investigating...)");
            this.investigationTarget = new Vector2(x, y);
            setStrategy(new InvestigateStrategy());
        }
    }
}
