package me.itstheholyblack.vigilant_eureka.core;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

public final class Polygon {
    private static final double MIN = Double.NEGATIVE_INFINITY;

    private static final double MAX = Double.POSITIVE_INFINITY;

    private static final DoubleBinaryOperator ADDITION = (a, b) -> a + b;

    private static final DoubleBinaryOperator MULTIPLICATION = (a, b) -> a * b;

    private final ImmutableList<Vec3d> vertices;

    private final int size;

    private final AxisAlignedBB bounds;

    private Polygon(ImmutableList<Vec3d> vertices, int size, AxisAlignedBB bounds) {
        this.vertices = vertices;
        this.size = size;
        this.bounds = bounds;
    }

    public Polygon add(double value) {
        return add(value, value);
    }

    public Polygon add(double x, double z) {
        return transform(ADDITION, x, z);
    }

    public Polygon scale(double value) {
        return scale(value, value);
    }

    public Polygon scale(double x, double z) {
        return transform(MULTIPLICATION, x, z);
    }

    private Polygon transform(DoubleBinaryOperator function, double x, double z) {
        return transform(applyWith(function, x), applyWith(function, z));
    }

    private Polygon transform(DoubleUnaryOperator xFunction, DoubleUnaryOperator zFunction) {
        Builder bob = builder();
        for (Vec3d vertex : vertices) {
            bob.add(xFunction.applyAsDouble(vertex.x), zFunction.applyAsDouble(vertex.z));
        }
        return bob.build();
    }

    public ImmutableList<Vec3d> getVertices() {
        return vertices;
    }

    public List<Entity> getEntities(World world) {
        return getEntities(world, Entity.class);
    }

    public <T extends Entity> List<T> getEntities(World world, Class<? extends T> clazz) {
        return getEntities(world, clazz, Predicates.alwaysTrue());
    }

    public <T extends Entity> List<T> getEntities(World world, Class<? extends T> clazz, Predicate<? super T> filter) {
        return world.getEntitiesWithinAABB(clazz, bounds.setMaxY(world.getHeight()), Predicates.and(this::contains, filter));
    }

    public boolean contains(Entity entity) {
        return contains(entity.getEntityBoundingBox());
    }

    public boolean contains(AxisAlignedBB box) {
        return contains(box.getCenter()) ||
                contains(box.minX, box.minZ, box.maxX, box.minZ) ||
                contains(box.maxX, box.minZ, box.maxX, box.maxZ) ||
                contains(box.maxX, box.maxZ, box.minX, box.maxZ) ||
                contains(box.minX, box.maxZ, box.minX, box.minZ);
    }

    public boolean contains(double x1, double z1, double x2, double z2) {
        return contains(new Vec3d(x1, 0, z1), new Vec3d(x2, 0, z2));
    }

    public boolean contains(Vec3d p, Vec3d q) {
        for (int i = 0; i < size; i++) {
            Vec3d v1 = vertices.get(i);
            Vec3d v2 = vertices.get((i + 1) % size);
            if (intersect(v1, v2, p, q)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Vec3d point) {
        return contains(point.x, point.z);
    }

    public boolean contains(double x, double z) {
        boolean inside = false;
        for (int i = 0, j = size - 1; i < size; j = i++) {
            Vec3d vi = vertices.get(i), vj = vertices.get(j);
            if (((vi.z > z) != (vj.z > z)) && (x < (vj.x - vi.x) * (z - vi.z) / (vj.z - vi.z) + vi.x)) {
                inside = !inside;
            }
        }
        return inside;
    }

    public static Builder builder() {
        return new Builder();
    }

    private static boolean intersect(Vec3d p1, Vec3d q1, Vec3d p2, Vec3d q2) {
        int o1 = getOrientation(p1, q1, p2);
        int o2 = getOrientation(p1, q1, q2);
        int o3 = getOrientation(p2, q2, p1);
        int o4 = getOrientation(p2, q2, q1);
        return o1 != o2 && o3 != o4 ||
                o1 == 0 && isOnSegment(p1, p2, q1) ||
                o2 == 0 && isOnSegment(p1, q2, q1) ||
                o3 == 0 && isOnSegment(p2, p1, q2) ||
                o4 == 0 && isOnSegment(p2, q1, q2);
    }

    private static boolean isOnSegment(Vec3d p, Vec3d q, Vec3d r) {
        return q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) && q.z <= Math.max(p.z, r.z) && q.z >= Math.min(p.z, r.z);
    }

    private static int getOrientation(Vec3d p, Vec3d q, Vec3d r) {
        double val = (q.z - p.z) * (r.x - q.x) - (q.x - p.x) * (r.z - q.z);
        return val == 0 ? 0 : (val > 0) ? 1 : 2;
    }

    private static DoubleUnaryOperator applyWith(DoubleBinaryOperator operator, double right) {
        return left -> operator.applyAsDouble(left, right);
    }

    public static final class Builder {
        private static final int MIN_VERTICES = 3;

        private final ImmutableList.Builder<Vec3d> vertices = ImmutableList.builder();

        private Builder() {
        }

        public Builder addAll(Vec3d... points) {
            for (Vec3d point : points) {
                add(point);
            }
            return this;
        }

        public Builder add(Vec3d point) {
            return add(point.x, point.z);
        }

        public Builder add(double x, double z) {
            checkFinite(x, "x");
            checkFinite(z, "z");
            vertices.add(new Vec3d(x, 0, z));
            return this;
        }

        public Polygon build() {
            ImmutableList<Vec3d> vertices = this.vertices.build();
            int size = vertices.size();
            if (size < MIN_VERTICES) {
                throw new IllegalArgumentException("Too few vertices: " + size + ", minimum is " + MIN_VERTICES);
            }
            AxisAlignedBB bounds = computeBounds(vertices);
            return new Polygon(vertices, size, bounds);
        }

        private static AxisAlignedBB computeBounds(Iterable<? extends Vec3d> vectors) {
            Set<Vec3d> seen = Sets.newHashSet();
            double minX = MAX, minZ = MAX;
            double maxX = MIN, maxZ = MIN;
            for (Vec3d vec : vectors) {
                if (!seen.add(vec)) {
                    throw new IllegalArgumentException("Duplicate vertex: " + vec);
                }
                minX = Math.min(minX, vec.x);
                minZ = Math.min(minZ, vec.z);
                maxX = Math.max(maxX, vec.x);
                maxZ = Math.max(maxZ, vec.z);
            }
            return new AxisAlignedBB(minX, 0, minZ, maxX, 0, maxZ);
        }

        private static void checkFinite(double value, String name) {
            Preconditions.checkArgument(Double.isFinite(value), name + " must be finite");
        }
    }
}