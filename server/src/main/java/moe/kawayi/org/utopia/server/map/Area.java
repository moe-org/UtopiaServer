//* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
// The Area.java is a part of project utopia, under MIT License.
// See https://opensource.org/licenses/MIT for license information.
// Copyright (c) 2021 moe-org All rights reserved.
//* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

package moe.kawayi.org.utopia.server.map;

import moe.kawayi.org.utopia.core.util.NotNull;

import java.util.Optional;

/**
 * 地图区域
 * <p>
 * 线程安全
 */
public interface Area {

    /**
     * 获取地图块
     * <p>
     * 位置范围: from 0 to ([X|Y]_SIZE-1)
     *
     * @param position 要获取的地图块位置
     * @return 获取到的地图块。如果地图块超出范围，则返回空
     * @see WorldInfo#BLOCK_FLOOR_X_SIZE
     * @see WorldInfo#BLOCK_FLOOR_Y_SIZE
     */
    @NotNull
    Optional<Block> getBlock(@NotNull Position position);
}
