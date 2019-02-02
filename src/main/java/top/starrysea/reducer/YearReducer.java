package top.starrysea.reducer;

import top.starrysea.dto.Count;
import top.starrysea.mapreduce.MapReduceContext;
import top.starrysea.mapreduce.ReduceResult;
import top.starrysea.mapreduce.reducer.LongReducer;
import top.starrysea.repository.CountRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class YearReducer extends LongReducer {

	@Autowired
	private CountRepository countRepository;

	@Override
	protected ReduceResult<Long> reduce(File path) {
		long count = 0;
		try (Stream<String> line = Files.lines(path.toPath())) {
			count = line.count();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		String date = path.getName();
		date = date.substring(0, date.indexOf('-'));
		return ReduceResult.of(date, count);
	}

	@Override
	protected void reduceFinish(Map<String, Long> reduceResult, MapReduceContext context) {
		Count count = new Count();
		count.setType("year");
		count.setResult(reduceResult);
		countRepository.findById("year").defaultIfEmpty(count).subscribe(chatCountTemp -> {
			chatCountTemp.getResult().putAll(reduceResult);
			countRepository.save(chatCountTemp).subscribe();
			logger.info("每年分析已存入数据库.");
		});
	}
}
