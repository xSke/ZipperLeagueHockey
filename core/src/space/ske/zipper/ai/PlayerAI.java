package space.ske.zipper.ai;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import space.ske.zipper.Zipper;
import space.ske.zipper.entity.Player;
import space.ske.zipper.entity.Puck;
import space.ske.zipper.util.SpringingContext2D;

public class PlayerAI extends AI {
    private Array<Vector2> points = new Array<Vector2>();
    private SpringingContext2D targetPoint = new SpringingContext2D(1, 1);
    private float timer;
    private float canShootTimer;

    public enum Role {
        ATTACK,
        DEFENSE
    }

    private Role role;
    private boolean shouldShoot;
    private boolean isPassing;

    public PlayerAI() {
        timer = MathUtils.random(0f, 2f);
        recalculatePoints();
    }

    @Override
    public Vector2 update(float dt) {
        timer += dt;

        if (timer > 2) {
            timer -= 2;

            recalculatePoints();
            if (MathUtils.randomBoolean()) {
                role = Role.ATTACK;
            } else {
                role = Role.DEFENSE;
            }
        }

        Vector2 outMovement = new Vector2();

        Puck puck = Zipper.i.getPuck();
        if (puck.getAnchor() == null && role != Role.DEFENSE) {
            angleTowards(outMovement, puck.body.getPosition().cpy());
        } else {
            targetPoint.target.set(bestPoint());
            targetPoint.update(dt);
            angleTowards(outMovement, targetPoint.value);
        }

        if (puck.getAnchor() == this.player) {
            canShootTimer += dt;
            shouldShoot = hasCleanShot();

            if (shouldShoot) {
                angleTowards(outMovement, getGoalPosition(true));
            }

            if (canShootTimer > 4) {
                shouldShoot = true;

                if (MathUtils.randomBoolean(0.7f)) {
                    angleTowards(outMovement, teammateToPass().body.getPosition());
                    isPassing = true;
                } else {
                    angleTowards(outMovement, getGoalPosition(true));
                }
            }
        } else {
            canShootTimer = 0;
            isPassing = false;
        }

        return outMovement;
    }

    private Player teammateToPass() {
        Array<Player> mates = new Array<Player>();
        for (Player player : Zipper.i.getPlayers()) {
            if (player != this.player && player.isTeam() == this.player.isTeam()) {
                mates.add(player);
            }
        }
        return mates.random();
    }

    private void recalculatePoints() {
        points.clear();
        for (int i = 0; i < 70; i++) {
            points.add(new Vector2(
                    MathUtils.random(13, 47),
                    MathUtils.random(8, 46)
            ));
        }
    }

    private Vector2 bestPoint() {
        float max = -999;
        Vector2 maxPoint = null;
        for (Vector2 point : points) {
            float rank = rankPoint(point);
            if (rank > max) {
                max = rank;
                maxPoint = point;
            }
        }
        return maxPoint;
    }

    private float rankPoint(Vector2 point) {
        float rank;
        if (Zipper.i.getFocus() == null) return 0;
        if (Zipper.i.getPuck().getAnchor() == player) {
            Vector2 goalPosition = getGoalPosition(true);
            float proximityToGoalY = Math.abs(goalPosition.y - point.y);
            rank = 50 - proximityToGoalY;
        } else if (Zipper.i.getFocus().isTeam() == player.isTeam() && role != Role.DEFENSE) {
            Vector2 focusPosition = Zipper.i.getFocus().body.getPosition();

            float playerYDelta = (focusPosition.y - point.y) * (player.isTeam() ? 1 : -1);
            float playerXDelta = focusPosition.x - point.x;

            float yRank = 1 / Math.abs(playerYDelta - 5);
            float xRank = (float) (1 / Math.pow(Math.abs(Math.abs(playerXDelta) - 5), 2));
            float goalProximityRank = 0;
            rank = yRank * 2;
        } else {
            Vector2 focusPosition = Zipper.i.getFocus().body.getPosition();
            Vector2 goalPosition = getGoalPosition(false);

            Vector2 middle = new Vector2(goalPosition);
            if (role == Role.DEFENSE) {
                middle.mulAdd(focusPosition, 4).scl(0.2f);
            } else {
                middle.add(focusPosition).scl(0.5f);
            }
            rank = 50 - middle.dst(point);
        }

        for (Player player : Zipper.i.getPlayers()) {
            if (player == this.player) continue;

            float dst = player.body.getPosition().dst(point);
            if (dst < 4) {
                rank *= dst / 4;
            }
        }
        return rank;
    }

    private Vector2 getGoalPosition(boolean toTarget) {
        boolean team = player.isTeam();
        if (toTarget) team = !team;
        return team ? new Vector2(30, 46) : new Vector2(30, 8);
    }

    public void angleTowards(Vector2 out, Vector2 pos) {
        if (player.body.getPosition().dst2(pos) < 1) {
            out.set(0, 0);
        } else {
            out.set(pos).sub(player.body.getPosition());
        }
    }

    @Override
    public boolean shouldShoot() {
        if (Zipper.i.getPuck().getAnchor() == player) {
            Vector2 thisPos = player.body.getPosition();
            Vector2 goalPos = getGoalPosition(true);
            Vector2 toGoalVec = goalPos.cpy().sub(thisPos);
            boolean correctAngle = Math.abs(toGoalVec.angleRad() - player.body.getAngle()) < 0.1f;
            if (isPassing) correctAngle = true;
            return shouldShoot && correctAngle;
        }
        return false;
    }

    private boolean hasCleanShot() {
        Vector2 thisPos = player.body.getPosition();
        Vector2 goalPos = getGoalPosition(true);

        boolean hasClearShot = true;
        for (Player player : Zipper.i.getPlayers()) {
            if (player == this.player) continue;
            if (player.isTeam() == this.player.isTeam()) continue;

            Vector2 playerPos = player.body.getPosition();
            if (Intersector.distanceLinePoint(thisPos.x, thisPos.y, goalPos.x, goalPos.y, playerPos.x, playerPos.y) < 1) {
                hasClearShot = false;
            }
        }

        Vector2 toGoalVec = goalPos.cpy().sub(thisPos);
        boolean isCloseEnough = toGoalVec.len() < 20;
        return hasClearShot && isCloseEnough;
    }
}
