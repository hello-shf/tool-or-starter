package com.my.datasource.core;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.my.datasource.core.vm.SmartPageVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.List;


public class BaseController<S extends IService<T>, T extends BaseModel<T>> {
	protected HttpServletRequest request;

	protected HttpServletResponse response;

	protected HttpSession session;

	@Autowired
	protected S service;

	/**
	 * 分页查询数据
	 * @param spage
	 * @return
	 */
	@PostMapping("/queryListByPage")
	public IPage<T> getSmartData(@RequestBody SmartPageVM<T> spage) {
		QueryWrapper<T> wrapper = new QueryWrapper<>();
		Page<T> page = spage.getPage();
		//设置默认排序条件
		if (StrUtil.isBlank(spage.getSort().getPredicate())) {
			spage.getSort().setPredicate("id");
		}
		//判断升序降序
		if(spage.getSort().getReverse()){
			page.setAsc(spage.getSort().getPredicate());
			wrapper.orderByAsc(spage.getSort().getPredicate());
		}else{
			page.setDesc(spage.getSort().getPredicate());
			wrapper.orderByDesc(spage.getSort().getPredicate());
		}
		if (spage.getSearch() != null) {
			Field[] fields = spage.getSearch().getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				try {
					fields[i].setAccessible(true);
					Object value = fields[i].get(spage.getSearch());
					if (null != value && !value.equals("")) {
						wrapper.eq(StrUtil.toUnderlineCase(fields[i].getName()), value.toString());
					}
					fields[i].setAccessible(false);
				} catch (Exception e) {
				}
			}
		}
		IPage<T> tPage = service.page(page, wrapper);
        return  tPage;
	}

	/**
	 * 条件查询数据
	 * @param t
	 * @return
	 */
	@PostMapping("/queryList")
	public List<T> getAllData(@RequestBody T t) {
		QueryWrapper<T> wrapper = new QueryWrapper<>();
		Field[] fields = t.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				fields[i].setAccessible(true);
				Object value = fields[i].get(t);
				if (null != value && !value.equals("")) {
					wrapper.eq(StrUtil.toUnderlineCase(fields[i].getName()), value.toString());
				}
				fields[i].setAccessible(false);
			} catch (Exception e) {
			}
		}
		return service.list(wrapper);
	}


	/**
	 * 条件查询数据
	 * @param t
	 * @return
	 */
	@PostMapping("/querySingle")
	public T querySingle(@RequestBody T t) {
		QueryWrapper<T> wrapper = new QueryWrapper<>();
		Field[] fields = t.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				fields[i].setAccessible(true);
				Object value = fields[i].get(t);
				if (null != value && !value.equals("")) {
					wrapper.eq(StrUtil.toUnderlineCase(fields[i].getName()), value.toString());
				}
				fields[i].setAccessible(false);
			} catch (Exception e) {
			}
		}
		return service.getOne(wrapper,false);
	}

	@PostMapping("/querySingleByWrapper")
	public T querySingleByWrapper(@RequestBody QueryWrapper<T> wrapper) {
		return service.getOne(wrapper,true);
	}

	@PostMapping("/queryListByWrapper")
	public List<T> queryListByWrapper(@RequestBody QueryWrapper<T> wrapper) {
		return service.list(wrapper);
	}

	/**
	 * 新增
	 * @param t
	 * @return
	 */
	@PostMapping("/add")
	@Transactional
	public boolean create(@RequestBody T t) {
		//t.setCreateUserId(ShiroUtils.getUserId());
//		t.setCreateTime(new Date());
//		t.setUpdateTime(new Date());
			//t.setUpdateUserId(ShiroUtils.getUserId());
		return service.save(t);
	}
	@PostMapping("/addGetObj")
	@Transactional
	public T createReturnObj(@RequestBody T t) {
		service.save(t);
		return t;
	}

	/**
	 * 更新
	 * @param t
	 * @return
	 */
	@PostMapping("/updateById")
	@Transactional
	public boolean update(@RequestBody T t) {
//		t.setUpdateTime(new Date());
		//t.setUpdateUserId(ShiroUtils.getUserId());
		return service.updateById(t);
	}

	/**
	 * 根据id获取实体对象
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	public T getInfo(@PathVariable String id) {
		return service.getById(id);
	}

	/**
	 * 删除
	 * @param id
	 * @return
	 */
	@Deprecated
	public boolean delete(String id) {
		return service.removeById(id);
	}

	/**
	 * 方法描述：得到PageData
	 * @return
	 * @return: ParamMap
	 */
	public ParamMap getTransmitMap() {
		return new ParamMap(this.getRequest());
	}

	/**
	 * 方法描述：得到request对象
	 * @return
	 * @return: HttpServletRequest
	 */
	public HttpServletRequest getRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return request;
	}
}
