package me.armar.plugins.utils.pluginlibrary.hooks;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.PlayerPoints;
import me.armar.plugins.utils.pluginlibrary.Library;
import me.armar.plugins.utils.pluginlibrary.util.Util;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class JobsHook extends LibraryHook {
    public JobsHook() {
    }

    public boolean isHooked() {
        return isPluginAvailable(Library.JOBS);
    }

    public boolean hook() {
        return isPluginAvailable(Library.JOBS);
    }

    public double getCurrentPoints(UUID uuid) {
        if (this.isHooked() && uuid != null) {
            PlayerPoints pointInfo = Jobs.getPlayerManager().getPlayerInfo(uuid).getJobsPlayer().getPointsData();
            return pointInfo == null ? -1.0D : Util.roundDouble(pointInfo.getCurrentPoints(), 2);
        } else {
            return -1.0D;
        }
    }

    public double getTotalPoints(UUID uuid) {
        if (this.isHooked() && uuid != null) {
            PlayerPoints pointInfo = Jobs.getPlayerManager().getPlayerInfo(uuid).getJobsPlayer().getPointsData();
            return pointInfo == null ? -1.0D : Util.roundDouble(pointInfo.getTotalPoints(), 2);
        } else {
            return -1.0D;
        }
    }

    public double getCurrentXP(Player player, String jobName) {
        if (!this.isHooked()) {
            return -1.0D;
        } else {
            Job job = this.getJob(jobName);
            if (job == null) {
                return -1.0D;
            } else {
                JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
                if (jobsPlayer == null) {
                    return -1.0D;
                } else {
                    JobProgression progress = jobsPlayer.getJobProgression(job);
                    return progress == null ? -1.0D : Util.roundDouble(progress.getExperience(), 2);
                }
            }
        }
    }

    public double getCurrentLevel(Player player, String jobName) {
        if (!this.isHooked()) {
            return -1.0D;
        } else {
            Job job = this.getJob(jobName);
            if (job == null) {
                return -1.0D;
            } else {
                JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
                if (jobsPlayer == null) {
                    return -1.0D;
                } else {
                    JobProgression progress = jobsPlayer.getJobProgression(job);
                    return progress == null ? -1.0D : Util.roundDouble(progress.getLevel(), 2);
                }
            }
        }
    }

    public Job getJob(String jobName) {
        return !this.isHooked() ? null : Jobs.getJob(jobName);
    }

    public List<JobProgression> getJobs(Player player) {
        if (!this.isHooked()) {
            return null;
        } else {
            JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
            return jobsPlayer == null ? null : jobsPlayer.getJobProgression();
        }
    }
}
