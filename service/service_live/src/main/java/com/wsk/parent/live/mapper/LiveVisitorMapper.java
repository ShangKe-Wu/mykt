package com.wsk.parent.live.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wsk.ggkt.model.live.LiveVisitor;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 直播来访者记录表 Mapper 接口
 * </p>
 *
 * @author wsk
 * @since 2022-08-22
 */
@Mapper
public interface LiveVisitorMapper extends BaseMapper<LiveVisitor> {

}
